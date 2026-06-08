package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.*;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Inventory;
import com.shikhilrane.project.airBnbApp.entity.Room;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.repository.HotelMinPriceRepository;
import com.shikhilrane.project.airBnbApp.repository.InventoryRepository;
import com.shikhilrane.project.airBnbApp.repository.RoomRepository;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.shikhilrane.project.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;          // Performs inventory database operations
    private final ModelMapper modelMapper;                          // Converts entities and DTOs
    private final HotelMinPriceRepository hotelMinPriceRepository;  // Performs optimized hotel search queries
    private final RoomRepository roomRepository;                    // Performs room database operations

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

    // Retrieves all inventory records of a room after ownership validation
    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {

        log.info("Getting all inventory for room with id: {}", roomId);      // Logs inventory retrieval request

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId)); // Fetches room or throws exception

        User user = getCurrentUser();                                         // Retrieves currently authenticated user

        if (!user.getId().equals(room.getHotel().getOwner().getId())) {
            throw new AccessDeniedException("You are not the owner of room with id: " + roomId); // Validates room ownership
        }

        return inventoryRepository.findByRoomOrderByDate(room)
                .stream()
                .map(inventory ->
                        modelMapper.map(inventory, InventoryDto.class))        // Converts inventory entities into DTOs
                .collect(Collectors.toList());
    }

    // Updates inventory settings for a room within a date range
    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {

        log.info("Updating All inventory by room for room with id: {} between date range: {} - {}",
                roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate());                       // Logs inventory update request

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId)); // Fetches room or throws exception

        User user = getCurrentUser();                                          // Retrieves currently authenticated user

        if (!user.getId().equals(room.getHotel().getOwner().getId()))
            throw new AccessDeniedException("You are not the owner of room with id: " + roomId); // Validates room ownership

        inventoryRepository.getInventoryAndLockBeforeUpdate(
                roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate()
        );                                                                     // Locks inventory records before update

        inventoryRepository.updateInventory(
                roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(),
                updateInventoryRequestDto.getClosed(),
                updateInventoryRequestDto.getSurgeFactor()
        );                                                                     // Updates inventory availability and surge factor
    }
}

/*
    InventoryServiceImpl

        Purpose :
            Handles inventory management, inventory updates,
            and hotel search operations.

        Responsibilities :
            - Generate room inventory
            - Delete room inventory
            - Search hotels
            - Retrieve inventory details
            - Update inventory settings
            - Validate room ownership
            - Manage inventory pricing
            - Manage room availability

        Methods :

            initializeRoomForAYear()
                - Creates inventory records for one year
                - Initializes pricing and availability
                - Creates one inventory record per day

            deleteFutureInventories()
                - Deletes inventory associated with a room

            searchHotels()
                - Searches hotels by city
                - Filters by date range
                - Filters by room count
                - Returns paginated results

            getAllInventoryByRoom()
                - Retrieves inventory of a room
                - Validates room ownership

            updateInventory()
                - Updates inventory configuration
                - Updates surge pricing
                - Updates booking availability
                - Locks inventory before update

        Inventory Lifecycle :

            Room Created
                    ↓
        initializeRoomForAYear()
                    ↓
          Daily Inventory Records
                    ↓
             Available For Booking

        Hotel Search Flow :

            Guest Search Request
                    ↓
                 City
                    +
               Date Range
                    +
              Required Rooms
                    ↓
         Hotel Availability Search
                    ↓
          HotelMinPriceRepository
                    ↓
            Matching Hotels
                    ↓
             API Response

        Inventory Retrieval Flow :

            Hotel Manager
                    ↓
            Room Selection
                    ↓
        Ownership Validation
                    ↓
            Fetch Inventory
                    ↓
            InventoryDto List
                    ↓
             API Response

        Inventory Update Flow :

            Hotel Manager
                    ↓
            Select Room
                    ↓
            Select Date Range
                    ↓
        Ownership Validation
                    ↓
            Lock Inventory
                    ↓
          Update Inventory
                    ↓
             Commit Changes

        Pricing Flow :

            Base Room Price
                    ↓
             Surge Factor
                    ↓
             Final Price
                    ↓
           Search Results

        Availability Flow :

            closed = false
                    ↓
            Accept Bookings

            closed = true
                    ↓
            Reject Bookings

        Ownership Validation Flow :

            Authenticated User
                    ↓
            Room Owner Check
                    ↓
            Access Granted / Denied

        Business Use :
            - Hotel search
            - Inventory management
            - Dynamic pricing
            - Room availability management
            - Hotel manager operations
            - Booking preparation

        Security Features :
            - Room ownership validation
            - Access control enforcement
            - Pessimistic inventory locking
            - Transaction-safe inventory updates

        Note :
            - Inventory is generated for one year.
            - One inventory record exists per room per day.
            - Search results are paginated.
            - Inventory updates are performed inside transactions.
            - Inventory records are locked before updates.
            - Hotel search uses HotelMinPriceRepository for optimized performance.
            - Only room owners can manage inventory.

        Data Relationships :

            Hotel
                ↓
              Room
                ↓
           Inventory
                ↓
         Availability
                +
             Pricing

        This service acts as the central
        inventory management and hotel
        search component of the application.
*/