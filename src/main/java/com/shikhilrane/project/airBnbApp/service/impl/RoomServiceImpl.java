package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.RoomDto;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Room;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.exception.UnAuthorisedException;
import com.shikhilrane.project.airBnbApp.repository.HotelRepository;
import com.shikhilrane.project.airBnbApp.repository.RoomRepository;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import com.shikhilrane.project.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;        // Performs room database operations
    private final HotelRepository hotelRepository;      // Performs hotel database operations
    private final InventoryService inventoryService;    // Manages room inventory operations
    private final ModelMapper modelMapper;              // Converts DTOs into entities and vice versa

    // Creates a new room inside a hotel after validating hotel ownership
    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID: {}", hotelId);              // Logs room creation request
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId)); // Fetches hotel or throws exception
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();       // Fetches currently authenticated HOTEL_MANAGER
        // Verifies that hotel belongs to the current user
        if (!user.equals(hotel.getOwner())) {
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
        if (!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id " + hotelId);    // Prevents unauthorized hotel access
        }
        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))    // Converts each Room Entity into DTO
                .collect(Collectors.toList());                                      // Returns list of room DTOs
    }

    // Returns room details using room ID
    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room with ID: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));   // Fetches room or throws exception
        return modelMapper.map(room, RoomDto.class);                                // Converts Entity to DTO
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
        if (!user.equals(room.getHotel().getOwner())) {
            throw new UnAuthorisedException("This user does not own this room with id " + roomId);      // Prevents unauthorized room access
        }
        inventoryService.deleteFutureInventories(room);                             // Deletes inventory records associated with room
        roomRepository.deleteById(roomId);                                          // Deletes room from database
    }
}


/*
    RoomServiceImpl

    Purpose : Handles room-related business operations.
              Acts as a bridge between the Controller and Repository layers.

    Responsibilities :
        - Create rooms inside hotels
        - Fetch hotel rooms
        - Fetch room details
        - Delete rooms
        - Generate room inventory
        - Validate hotel ownership
        - Validate room ownership

    Methods :

        createNewRoom()
            - Creates a room inside a hotel
            - Verifies hotel ownership
            - Generates inventory if hotel is active

        getAllRoomsInHotel()
            - Fetches all rooms belonging to a hotel
            - Verifies hotel ownership

        getRoomById()
            - Fetches room details using room ID

        deleteRoomById()
            - Verifies room ownership
            - Deletes associated inventories
            - Deletes room from database

    Business Use :
        - Allows HOTEL_MANAGER to manage rooms
        - Supports room creation and deletion
        - Supports hotel inventory generation
        - Maintains room availability lifecycle
        - Prevents unauthorized room access
        - Ensures only hotel owners manage rooms

    Room Lifecycle :

        Room Created
                ↓
           Assigned To
              Hotel
                ↓
         Hotel Activated
                ↓
       Inventory Generated
                ↓
      Available For Booking

        OR

        Room Deleted
                ↓
      Inventory Deleted
                ↓
        Room Removed

    Room Ownership Flow :

        HOTEL_MANAGER Login
                ↓
           Select Hotel
                ↓
        Ownership Check
                ↓
          Create Room
                ↓
       Inventory Generated
                ↓
         Manage Room
                ↓
          Delete Room

    Authorization Rules :

        Hotel Owner
                ↓
        Can Create Rooms

        Can View Rooms

        Can Delete Rooms

        Other Users
                ↓
        Access Denied

    Inventory Generation Flow :

        Active Hotel
                ↓
          Room Created
                ↓
    initializeRoomForAYear()
                ↓
     Create Daily Inventory
                ↓
         Next 365 Days

    Security Features :
        - Hotel ownership validation
        - Room ownership validation
        - Unauthorized access prevention
        - Role-based access control
        - Secure room management

    Note :
        - Only hotel owners can create rooms.
        - Only hotel owners can view hotel rooms.
        - Only hotel owners can delete rooms.
        - Inventory is automatically generated for active hotels.
        - Room deletion removes associated inventory records.
        - Inventory generation is delegated to InventoryService.

    This class acts as the business layer
    for room management.
*/