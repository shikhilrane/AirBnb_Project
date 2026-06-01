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
@RequestMapping(path = "/admin/hotels/{hotelId}/rooms")
public class RoomAdminController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@Valid @RequestBody RoomDto roomDto, @PathVariable Long hotelId){
        RoomDto newRoom = roomService.createNewRoom(hotelId, roomDto);              // Creates a new room in the hotel
        return new ResponseEntity<>(newRoom, HttpStatus.CREATED);                   // Returns created room with 201 status
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms(@PathVariable Long hotelId){
        List<RoomDto> allRoomsInHotel = roomService.getAllRoomsInHotel(hotelId);    // Fetches all rooms of the hotel
        return ResponseEntity.ok(allRoomsInHotel);                                  // Returns room list with 200 status
    }

    @GetMapping(path = "/{roomId}")
    public ResponseEntity<RoomDto> getRoomByHotelId(@PathVariable Long roomId){
        RoomDto roomById = roomService.getRoomById(roomId);                         // Fetches room details by room ID
        return ResponseEntity.ok(roomById);                                         // Returns room data with 200 status
    }

    @DeleteMapping(path = "/{roomId}")
    public ResponseEntity<Void> deleteRoomByRoomId(@PathVariable Long roomId){
        roomService.deleteRoomById(roomId);                                         // Deletes room by ID
        return ResponseEntity.noContent().build();                                  // Returns 204 No Content
    }
}

/*
    RoomAdminController

        Purpose : Handles room management operations for hotel administrators.
                  Acts as the entry point for room-related API requests.

        Responsibilities :
            - Create a room in a hotel
            - Get all rooms of a hotel
            - Get room details by ID
            - Delete a room

        Endpoints :

            POST   /admin/hotels/{hotelId}/rooms
                - Creates a new room in the hotel

            GET    /admin/hotels/{hotelId}/rooms
                - Fetches all rooms of the hotel

            GET    /admin/hotels/{hotelId}/rooms/{roomId}
                - Fetches room details by room ID

            DELETE /admin/hotels/{hotelId}/rooms/{roomId}
                - Deletes a room and its inventory records

        Flow :

            Client Request
                  ↓
            RoomAdminController
                  ↓
            RoomService
                  ↓
            Repository Layer
                  ↓
            Database

        Example :

            Create Room
                POST /admin/hotels/1/rooms

            Get All Rooms
                GET /admin/hotels/1/rooms

            Get Room By ID
                GET /admin/hotels/1/rooms/10

            Delete Room
                DELETE /admin/hotels/1/rooms/10

        Business Use :
            - Allows hotel managers to manage room types
            - Supports room creation and removal
            - Provides room information for hotel management
            - Maintains room inventory lifecycle

        Note :
            - Every room belongs to a specific hotel.
            - Room deletion also removes related inventory records.
            - Controller only handles HTTP requests and responses.

        This controller is responsible for room management within a hotel.
*/