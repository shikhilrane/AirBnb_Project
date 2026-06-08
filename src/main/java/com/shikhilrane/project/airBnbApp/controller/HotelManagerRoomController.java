package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.RoomDto;
import com.shikhilrane.project.airBnbApp.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/rooms/{hotelId}/rooms")
public class HotelManagerRoomController {
    private final RoomService roomService;

    // Creates a new room in a hotel
    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@Valid @RequestBody RoomDto roomDto, @PathVariable Long hotelId){
        RoomDto newRoom = roomService.createNewRoom(hotelId, roomDto);              // Creates a new room in the hotel
        return new ResponseEntity<>(newRoom, HttpStatus.CREATED);                   // Returns created room with 201 status
    }

    // Retrieves all rooms of a hotel
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms(@PathVariable Long hotelId){
        List<RoomDto> allRoomsInHotel = roomService.getAllRoomsInHotel(hotelId);    // Fetches all rooms of the hotel
        return ResponseEntity.ok(allRoomsInHotel);                                  // Returns room list with 200 status
    }

    // Retrieves room details by room ID
    @GetMapping(path = "/{roomId}")
    public ResponseEntity<RoomDto> getRoomByHotelId(@PathVariable Long roomId){
        RoomDto roomById = roomService.getRoomById(roomId);                         // Fetches room details by room ID
        return ResponseEntity.ok(roomById);                                         // Returns room data with 200 status
    }

    // Deletes a room by room ID
    @DeleteMapping(path = "/{roomId}")
    public ResponseEntity<Void> deleteRoomByRoomId(@PathVariable Long roomId){
        roomService.deleteRoomById(roomId);                                         // Deletes room and related inventory records
        return ResponseEntity.noContent().build();                                  // Returns 204 No Content
    }

    // Updates room details by room ID
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoomById(@PathVariable Long hotelId, @PathVariable Long roomId,
                                                  @RequestBody RoomDto roomDto) {
        return ResponseEntity.ok(roomService.updateRoomById(hotelId, roomId, roomDto)); // Updates room information
    }
}

/*
    HotelManagerRoomController

        Purpose :
            Handles room management operations for hotel managers.
            Acts as the entry point for room-related APIs.

        Responsibilities :
            - Create rooms for a hotel
            - Retrieve hotel rooms
            - Retrieve room details
            - Update room information
            - Delete rooms

        Endpoints :

            POST /rooms/{hotelId}/rooms
                - Creates a new room

            GET /rooms/{hotelId}/rooms
                - Retrieves all rooms of a hotel

            GET /rooms/{hotelId}/rooms/{roomId}
                - Retrieves room details

            PUT /rooms/{hotelId}/rooms/{roomId}
                - Updates room information

            DELETE /rooms/{hotelId}/rooms/{roomId}
                - Deletes a room

        Room Management Flow :

            Hotel Manager
                    ↓
            Create Room
                    ↓
            Room Created
                    ↓
            Manage Room Details
                    ↓
            Update / Delete
                    ↓
            Room Inventory Updated

        Room Lifecycle :

            Room Created
                    ↓
                ACTIVE
                    ↓
            Available For Booking
                    ↓
            Room Updated
                    ↓
            Room Deleted

        Business Use :
            - Room inventory management
            - Hotel room configuration
            - Capacity planning
            - Booking availability management
            - Room maintenance

        Security Features :
            - Role-based access control
            - Hotel ownership validation
            - Controlled room modifications

        Note :
            - Every room belongs to a specific hotel.
            - Room deletion may remove related inventory records.
            - Business logic is delegated to the service layer.
            - Controller only handles HTTP requests and responses.

        This controller acts as the room
        management entry point for hotels.
*/