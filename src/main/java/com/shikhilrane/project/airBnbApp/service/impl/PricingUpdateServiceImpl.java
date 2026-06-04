package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.HotelMinPrice;
import com.shikhilrane.project.airBnbApp.entity.Inventory;
import com.shikhilrane.project.airBnbApp.repository.HotelMinPriceRepository;
import com.shikhilrane.project.airBnbApp.repository.HotelRepository;
import com.shikhilrane.project.airBnbApp.repository.InventoryRepository;
import com.shikhilrane.project.airBnbApp.service.PricingUpdateService;
import com.shikhilrane.project.airBnbApp.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateServiceImpl implements PricingUpdateService {

    // Scheduler to update the inventory and HotelMinPrice tables every hour

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;


    // Runs scheduler and updates prices for all hotels
    @Override
    // @Scheduled(cron = "*/60 * * * * *")
    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices() {
        int page = 0;                                       // Starting page number
        int batchSize = 100;                                // Number of hotels processed per batch

        while(true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));   // Fetches hotels in batches
            if(hotelPage.isEmpty()) {
                break;                                      // Stops when no more hotels are available
            }
            for (Hotel hotel : hotelPage.getContent()) {
                updateHotelPrices(hotel);                   // Updates pricing for each hotel
            }
            page++;                                         // Moves to next batch
        }
    }

    // Updates inventory prices and hotel minimum prices for a hotel
    private void updateHotelPrices(Hotel hotel) {
        log.info("Updating hotel prices for hotel ID: {}", hotel.getId());
        LocalDate startDate = LocalDate.now();                                              // Current date
        LocalDate endDate = LocalDate.now().plusYears(1);                        // One year from today

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);   // Fetches hotel inventory records

        updateInventoryPrices(inventoryList);                                               // Updates inventory prices

        updateHotelMinPrice(hotel, inventoryList, startDate, endDate);                      // Updates minimum hotel prices
    }

    // Calculates and updates dynamic prices in inventory records
    private void updateInventoryPrices(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);    // Calculates dynamic room price
            inventory.setPrice(dynamicPrice);                                               // Updates inventory price
        });
        inventoryRepository.saveAll(inventoryList);                                         // Saves updated inventory records
    }

    // Calculates minimum room price per day and updates HotelMinPrice table
    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        // Compute minimum price per day for the hotel
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        inventory -> inventory.getDate(),                                                   // Groups inventory by date
                        Collectors.mapping(
                                inventory -> inventory.getPrice(),                                          // Extracts room price
                                Collectors.minBy((price1, price2) -> price1.compareTo(price2))  // Finds minimum price
                        )
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));   // Creates Date -> Minimum Price map

        // Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));                                                         // Finds existing record or creates new one
            hotelPrice.setPrice(price);                                                                              // Sets minimum price
            hotelPrices.add(hotelPrice);                                                                             // Adds record to save list
        });

        // Save all HotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelPrices);                                                                // Saves all minimum price records
    }
}

/*
    PricingUpdateServiceImpl

        Purpose : Updates inventory prices and hotel minimum prices automatically.

        Responsibilities :
            - Run pricing scheduler
            - Update inventory prices
            - Calculate hotel minimum prices
            - Save updated pricing data

        Methods :

            updatePrices()
                - Runs periodically using scheduler
                - Processes hotels in batches
                - Updates pricing for all hotels

            updateHotelPrices()
                - Fetches hotel inventory
                - Updates inventory prices
                - Updates hotel minimum prices

            updateInventoryPrices()
                - Calculates dynamic prices
                - Updates inventory records

            updateHotelMinPrice()
                - Finds minimum room price per day
                - Updates HotelMinPrice table

        Business Use :
            - Keeps hotel prices updated automatically.
            - Supports dynamic pricing.
            - Improves hotel search performance.
            - Maintains HotelMinPrice cache table.

        Pricing Update Flow :

            Scheduler
                ↓
            Fetch Hotels
                ↓
            Fetch Inventory
                ↓
            Calculate Dynamic Price
                ↓
            Update Inventory
                ↓
            Find Daily Minimum Price
                ↓
            Update HotelMinPrice

        Example :

            Hotel A :
                Room 1 = ₹1000
                Room 2 = ₹800
                Room 3 = ₹1200

            Minimum Price : ₹800

            Stored In : HotelMinPrice

        Note :
            - Runs automatically using @Scheduled.
            - Processes hotels in batches.
            - Updates both Inventory and HotelMinPrice tables.

        This service acts as the pricing scheduler of the application.
*/