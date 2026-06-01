package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.HotelDto;
import com.shikhilrane.project.airBnbApp.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/hotels")
public class HotelAdminController {
    private final HotelService hotelService;

    // 1. Create new hotel
    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@Valid @RequestBody HotelDto hotelDto){  // Reads and validates hotel data from request body
        HotelDto newHotel = hotelService.createNewHotel(hotelDto);                          // Creates a new hotel
        return new ResponseEntity<>(newHotel, HttpStatus.CREATED);                          // Returns created hotel with 201 status
    }

    // 2. Get Hotel by id
    @GetMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable (name = "hotelId") Long id){ // Reads hotel ID from URL
        HotelDto hotelById = hotelService.getHotelById(id);                                 // Fetches hotel details
        return ResponseEntity.ok(hotelById);                                                // Returns hotel data with 200 status
    }

    // 3. Update the hotel by id
    @PutMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDto> updateHotel(@Valid @RequestBody HotelDto hotelDto, @PathVariable (name = "hotelId") Long id){
        HotelDto hotelToUpdate = hotelService.updateHotelById(id, hotelDto);                // Updates hotel details
        return ResponseEntity.ok(hotelToUpdate);                                            // Returns updated hotel data
    }

    // 4. Delete the hotel by id
    @DeleteMapping(path = "/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable (name = "hotelId") Long id){
        hotelService.deleteHotelById(id);                                                   // Deletes the hotel
        return ResponseEntity.noContent().build();                                          // Returns 204 No Content
    }

    // 5. Activate hotel and make it available for booking
    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        hotelService.activateHotel(hotelId);                                                // Changes hotel status from inactive to active
        return ResponseEntity.noContent().build();                                          // Returns 204 No Content
    }
}

/*
    HotelAdminController

        Purpose : Handles all hotel management operations for administrators.
                  Acts as the entry point for hotel-related API requests.

        Responsibilities :
            - Create a new hotel
            - Get hotel details by ID
            - Update hotel information
            - Delete a hotel
            - Activate a hotel

        Endpoints :

            POST   /admin/hotels
                - Creates a new hotel

            GET    /admin/hotels/{hotelId}
                - Fetches hotel details by ID

            PUT    /admin/hotels/{hotelId}
                - Updates hotel information

            DELETE /admin/hotels/{hotelId}
                - Deletes a hotel

            PATCH  /admin/hotels/{hotelId}
                - Activates a hotel

        Flow :

            Client Request
                  ↓
            Controller
                  ↓
            Service Layer
                  ↓
            Repository Layer
                  ↓
            Database

        Example :

            Create Hotel
                POST /admin/hotels

            Get Hotel
                GET /admin/hotels/1

            Update Hotel
                PUT /admin/hotels/1

            Delete Hotel
                DELETE /admin/hotels/1

            Activate Hotel
                PATCH /admin/hotels/1

        Business Use :

            Create Hotel
                - Creates a hotel in inactive state

            Activate Hotel
                - Makes hotel visible for bookings

            Update Hotel
                - Updates hotel details

            Delete Hotel
                - Removes hotel from the system

        Note :
            - Request body validation is handled using @Valid.
            - Business logic is delegated to the service layer.
            - Controller only handles HTTP requests and responses.
            - Newly created hotels are inactive by default.
            - Hotel activation is performed using PATCH.

        Hotel Lifecycle :

            Hotel Created
                    ↓
                INACTIVE
                    ↓
            PATCH /{hotelId}
                    ↓
                 ACTIVE
                    ↓
            Available For Booking

        This controller is responsible for managing hotels in the system.
*/