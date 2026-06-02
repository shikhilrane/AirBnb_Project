package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.HotelDto;
import com.shikhilrane.project.airBnbApp.dto.HotelSearchReuestDto;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Inventory;
import com.shikhilrane.project.airBnbApp.entity.Room;
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

    @Override
    public Page<HotelDto> searchHotels(HotelSearchReuestDto hotelSearchReuestDto) {
        log.info("Searching hotels for {} city, from {} to {}", hotelSearchReuestDto.getCity(), hotelSearchReuestDto.getStartDate(), hotelSearchReuestDto.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchReuestDto.getPage(), hotelSearchReuestDto.getSize());             // Creates pagination configuration
        long dateCount =
                ChronoUnit.DAYS.between(hotelSearchReuestDto.getStartDate(), hotelSearchReuestDto.getEndDate()) + 1;    // Calculates total stay duration in days
        Page<Hotel> hotelPage = inventoryRepository.findHotelsWithAvailableInventory(
                hotelSearchReuestDto.getCity(),             // City where user wants hotel
                hotelSearchReuestDto.getStartDate(),        // Check-in date
                hotelSearchReuestDto.getEndDate(),          // Check-out date
                hotelSearchReuestDto.getRoomsCount(),       // Number of rooms required
                dateCount,                                  // Total stay days
                pageable);                                  // Pagination details
        return hotelPage.map((element) -> modelMapper.map(element, HotelDto.class)); // Converts Hotel entities into HotelDto objects and returns paginated result
    }
}

/*
    InventoryServiceImpl

        Purpose : Manages inventory-related business operations.
                  Handles inventory creation, deletion, and hotel availability search.

        Responsibilities :
            - Generate inventory for rooms
            - Delete room inventory
            - Search hotels based on availability

        Methods :

            initializeRoomForAYear()
                - Creates inventory records for next 1 year
                - Generates one inventory entry per day
                - Initializes pricing and room availability

            deleteFutureInventories()
                - Deletes all inventory records
                  associated with a room

            searchHotels()
                - Searches hotels by city
                - Checks room availability for date range
                - Supports pagination
                - Returns only available hotels

        Business Use :
            - Maintains day-wise room inventory
            - Supports hotel search functionality
            - Tracks room availability
            - Prevents overbooking
            - Provides inventory data for bookings

        Search Flow :

            Guest Search Request
                    ↓
                 City
                    +
               Date Range
                    +
              Required Rooms
                    ↓
            Inventory Search
                    ↓
            Available Hotels
                    ↓
               HotelDto Page

        Inventory Generation Flow :

            Room Activated
                    ↓
            initializeRoomForAYear()
                    ↓
            Create Inventory
              For Each Day
                    ↓
               Next 365 Days

        Note :
            - One inventory record exists for each
              Room + Date combination.
            - Hotel search uses inventory records
              instead of room records.
            - Search results are paginated.

        This class acts as the business layer
        for inventory management and hotel search.
*/
