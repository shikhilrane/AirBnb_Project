package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.RoomDto;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Room;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.repository.HotelRepository;
import com.shikhilrane.project.airBnbApp.repository.RoomRepository;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import com.shikhilrane.project.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;


    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID: {}", hotelId);              // Logs room creation request
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId)); // Fetches hotel or throws exception
        Room mappedRoom = modelMapper.map(roomDto, Room.class);                     // Converts DTO into Entity
        mappedRoom.setHotel(hotel);                                                 // Associates room with hotel
        Room savedRoom = roomRepository.save(mappedRoom);                           // Saves room in database

        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(savedRoom);                     // Generates one year inventory if hotel is active
        }

        return modelMapper.map(savedRoom, RoomDto.class);                           // Converts Entity back to DTO
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));
        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))    // Converts each Room Entity into DTO
                .collect(Collectors.toList());                                      // Returns list of room DTOs
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room with ID: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));   // Fetches room or throws exception
        return modelMapper.map(room, RoomDto.class);                                // Converts Entity to DTO
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID: {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));   // Fetches room or throws exception
        inventoryService.deleteFutureInventories(room);                             // Deletes inventory records associated with room
        roomRepository.deleteById(roomId);                                          // Deletes room from database
    }
}


/*
    RoomServiceImpl

    Purpose : Implements room-related business operations.
              Acts as a bridge between the Controller and Repository layers.

    Responsibilities :
        - Create a new room
        - Get all rooms of a hotel
        - Get room details by ID
        - Delete a room
        - Generate inventory for active hotels

    Methods :

        createNewRoom()
            - Creates a room inside a hotel
            - Generates inventory if hotel is active

        getAllRoomsInHotel()
            - Fetches all rooms belonging to a hotel

        getRoomById()
            - Fetches room details using room ID

        deleteRoomById()
            - Deletes room inventory records
            - Deletes the room from the database

    Business Use :
        - Allows hotel managers to manage room types
        - Supports room creation and deletion
        - Automatically generates inventory for active hotels
        - Provides room information for hotel management
        - Maintains room availability lifecycle

    Room Lifecycle :

        Room Created
                ↓
        Added To Hotel
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

    This class acts as the business layer for room management.
*/