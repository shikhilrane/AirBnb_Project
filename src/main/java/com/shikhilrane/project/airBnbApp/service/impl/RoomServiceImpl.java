package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.RoomDto;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Room;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.exception.UnAuthorisedException;
import com.shikhilrane.project.airBnbApp.repository.HotelRepository;
import com.shikhilrane.project.airBnbApp.repository.InventoryRepository;
import com.shikhilrane.project.airBnbApp.repository.RoomRepository;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import com.shikhilrane.project.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.shikhilrane.project.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;        // Performs room database operations
    private final HotelRepository hotelRepository;      // Performs hotel database operations
    private final InventoryService inventoryService;    // Manages room inventory operations
    private final ModelMapper modelMapper;              // Converts DTOs into entities and vice versa
    private final InventoryRepository inventoryRepository;     // Performs inventory database operations

    // Creates a new room inside a hotel after validating hotel ownership
    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID: {}", hotelId);              // Logs room creation request
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId)); // Fetches hotel or throws exception
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();       // Fetches currently authenticated HOTEL_MANAGER
        // Verifies that hotel belongs to the current user
        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + hotelId);    // Prevents unauthorized hotel access
        }
        Room mappedRoom = modelMapper.map(roomDto, Room.class);                     // Converts DTO into Entity
        mappedRoom.setHotel(hotel);                                                 // Associates room with hotel
        Room savedRoom = roomRepository.save(mappedRoom);                           // Saves room in database

        // Generates inventory only for active hotels
        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(savedRoom);                     // Generates one year inventory if hotel is active
        }

        return modelMapper.map(savedRoom, RoomDto.class);                           // Converts Entity back to DTO
    }

    // Returns all rooms of a hotel after validating hotel ownership
    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();       // Fetches currently authenticated HOTEL_MANAGER
        // Verifies that hotel belongs to the current user
        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + hotelId);
        }
        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))    // Converts each Room Entity into DTO
                .collect(Collectors.toList());                                      // Returns list of room DTOs
    }

    // Returns room details using room ID after validating room ownership
    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room with ID: {}", roomId);                             // Logs room fetch request

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));    // Fetches room or throws exception

        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();                                                      // Fetches currently authenticated HOTEL_MANAGER

        // Verifies that room belongs to the current user's hotel
        if (!user.getId().equals(room.getHotel().getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this room with id " + roomId);            // Prevents unauthorized room access
        }

        return modelMapper.map(room, RoomDto.class);                                  // Converts Entity to DTO
    }

    // Deletes room and associated inventories after validating room ownership
    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));   // Fetches room or throws exception
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();       // Fetches currently authenticated HOTEL_MANAGER
        if (!user.getId().equals(room.getHotel().getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this room with id " + roomId);
        }
        inventoryService.deleteFutureInventories(room);                             // Deletes inventory records associated with room
        roomRepository.deleteById(roomId);                                          // Deletes room from database
    }

    // Updates room details after validating hotel and room ownership
    @Override
    @Transactional
    public RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto) {

        log.info("Updating the room with ID: {}", roomId);                    // Logs room update request

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId)); // Fetches hotel or throws exception

        User user = getCurrentUser();                                          // Retrieves currently authenticated user

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId)); // Fetches room or throws exception

        if (!room.getHotel().getId().equals(hotelId)) {
            throw new IllegalStateException("Room does not belong to hotel with id: " + hotelId); // Validates hotel-room association
        }

        if (!user.getId().equals(room.getHotel().getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: " + hotelId); // Validates hotel ownership
        }

        BigDecimal oldBasePrice = room.getBasePrice();                         // Stores existing room base price

        modelMapper.map(roomDto, room);                                        // Maps updated room details
        room.setId(roomId);                                                    // Preserves existing room identifier

        if (oldBasePrice.compareTo(room.getBasePrice()) != 0) {

            inventoryRepository.updateFutureInventoryPrices(
                    roomId,
                    room.getBasePrice(),
                    LocalDate.now()
            );                                                                 // Updates future inventory prices if base price changes
        }

        room = roomRepository.save(room);                                      // Persists updated room

        return modelMapper.map(room, RoomDto.class);                           // Converts entity into DTO
    }
}


/*
    RoomServiceImpl

        Purpose :
            Handles room management operations.
            Manages room lifecycle, ownership validation,
            inventory generation, and room updates.

        Responsibilities :
            - Create rooms inside hotels
            - Fetch hotel rooms
            - Fetch room details
            - Update room details
            - Delete rooms
            - Generate room inventory
            - Synchronize inventory pricing
            - Validate hotel ownership
            - Validate room ownership

        Methods :

            createNewRoom()
                - Creates room inside hotel
                - Validates hotel ownership
                - Generates inventory for active hotels

            getAllRoomsInHotel()
                - Retrieves all rooms of a hotel
                - Validates hotel ownership

            getRoomById()
                - Retrieves room details
                - Validates room ownership

            deleteRoomById()
                - Deletes room inventories
                - Deletes room
                - Validates room ownership

            updateRoomById()
                - Updates room information
                - Validates hotel ownership
                - Validates room ownership
                - Validates room belongs to hotel
                - Synchronizes future inventory pricing
                - Saves updated room

        Room Lifecycle :

            Room Created
                    ↓
            Assigned To Hotel
                    ↓
            Hotel Activated
                    ↓
            Inventory Generated
                    ↓
            Available For Booking
                    ↓
            Room Updated
                    ↓
            Inventory Price Sync

            OR

            Room Deleted
                    ↓
            Inventory Deleted
                    ↓
            Room Removed

        Room Update Flow :

            Update Request
                    ↓
            Ownership Validation
                    ↓
            Hotel-Room Validation
                    ↓
            Update Room
                    ↓
            Base Price Changed?
                  ↓      ↓
                Yes      No
                 ↓        ↓
          Update Inventory
                 ↓
             Save Room

        Ownership Validation Flow :

            Authenticated User
                    ↓
              Fetch Owner
                    ↓
               Compare IDs
                    ↓
            Access Granted / Denied

        Inventory Generation Flow :

            Active Hotel
                    ↓
              Room Created
                    ↓
        initializeRoomForAYear()
                    ↓
         Daily Inventory Records
                    ↓
              Next 365 Days

        Security Features :
            - Hotel ownership validation
            - Room ownership validation
            - Unauthorized access prevention
            - Access control enforcement

        Business Use :
            - Room management
            - Hotel administration
            - Inventory generation
            - Room pricing management
            - Inventory price synchronization

        Note :
            - Only hotel owners can manage rooms.
            - Inventory is automatically generated for active hotels.
            - Updating room base price updates future inventory pricing.
            - Inventory generation is delegated to InventoryService.
            - Inventory pricing synchronization is delegated to InventoryRepository.

        This service acts as the central
        room management component
        of the application.
*/