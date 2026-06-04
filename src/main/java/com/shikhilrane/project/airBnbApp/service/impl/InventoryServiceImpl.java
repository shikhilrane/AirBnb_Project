package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.HotelDto;
import com.shikhilrane.project.airBnbApp.dto.HotelPriceDto;
import com.shikhilrane.project.airBnbApp.dto.HotelSearchReuestDto;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Inventory;
import com.shikhilrane.project.airBnbApp.entity.Room;
import com.shikhilrane.project.airBnbApp.repository.HotelMinPriceRepository;
import com.shikhilrane.project.airBnbApp.repository.InventoryRepository;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();                            // Gets current date
        LocalDate endDate = today.plusYears(1);            // Calculates date after one year
        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())                // Associates inventory with hotel
                    .room(room)                            // Associates inventory with room
                    .date(date)                            // Sets inventory date
                    .bookedCount(0)                        // Initially no rooms are booked
                    .reservedCount(0)
                    .totalCount(room.getTotalCount())      // Sets total available rooms
                    .price(room.getBasePrice())            // Sets base room price
                    .surgeFactor(BigDecimal.ONE)           // Default surge factor = 1.0
                    .city(room.getHotel().getCity())       // Stores city for faster search
                    .closed(false)                         // Keeps booking open by default
                    .build();
            inventoryRepository.save(inventory);
        }                                                  // This loop generates daily inventory records for a room for the next 1 year.
    }

    @Override
    public void deleteFutureInventories(Room room) {
        inventoryRepository.deleteByRoom(room);            // Deletes all inventory records associated with the room
    }

    // Searches hotels and returns hotel pricing information
    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchReuestDto hotelSearchReuestDto) {
        log.info("Searching hotels for {} city, from {} to {}", hotelSearchReuestDto.getCity(), hotelSearchReuestDto.getStartDate(), hotelSearchReuestDto.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchReuestDto.getPage(), hotelSearchReuestDto.getSize());             // Creates pagination configuration
        long dateCount =
                ChronoUnit.DAYS.between(hotelSearchReuestDto.getStartDate(), hotelSearchReuestDto.getEndDate()) + 1;    // Calculates total stay duration in days

        // business logic - 90 days
        Page<HotelPriceDto> hotelPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(
                hotelSearchReuestDto.getCity(),             // City where user wants hotel
                hotelSearchReuestDto.getStartDate(),        // Check-in date
                hotelSearchReuestDto.getEndDate(),          // Check-out date
                hotelSearchReuestDto.getRoomsCount(),       // Number of rooms required
                dateCount,                                  // Total stay days
                pageable);                                  // Pagination details
        return hotelPage; // Converts Hotel entities into HotelDto objects and returns paginated result
    }
}

/*
    InventoryServiceImpl

        Purpose : Manages inventory-related business operations.
                  Handles inventory creation, deletion, and hotel search.

        Responsibilities :
            - Generate inventory records
            - Delete inventory records
            - Search hotels
            - Manage room availability

        Methods :

            initializeRoomForAYear()
                - Creates inventory for next 1 year
                - Generates one inventory record per day
                - Initializes room availability and pricing

            deleteFutureInventories()
                - Deletes inventory associated with a room

            searchHotels()
                - Searches hotels by city
                - Filters hotels by date range
                - Returns hotel pricing information
                - Supports pagination

        Business Use :
            - Maintains day-wise room inventory.
            - Supports hotel availability management.
            - Provides hotel search functionality.
            - Supplies inventory data for bookings.
            - Uses pre-calculated hotel pricing for faster searches.

        Search Flow :

            Guest Search Request
                    ↓
                 City
                    +
               Date Range
                    +
              Required Rooms
                    ↓
            HotelMinPrice Search
                    ↓
            Average Hotel Price
                    ↓
            HotelPriceDto Page

        Inventory Generation Flow :

            Room Created
                    ↓
            initializeRoomForAYear()
                    ↓
            Create Inventory
              For Each Day
                    ↓
               Next 366 Days

        Example :

            Room Created
                    ↓
            Inventory Generated
                    ↓
            04 Jun 2026
            05 Jun 2026
            06 Jun 2026
            ...
            03 Jun 2027

        Note :
            - One inventory record exists for each
              Hotel + Room + Date combination.
            - Search uses HotelMinPrice table for better performance.
            - Search results are paginated.
            - Inventory stores availability and pricing information.

        This class acts as the business layer for inventory management and hotel search.
*/