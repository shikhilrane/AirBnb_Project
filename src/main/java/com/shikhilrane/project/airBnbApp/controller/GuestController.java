package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.GuestDto;
import com.shikhilrane.project.airBnbApp.service.GuestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    // Creates a new guest record
    @PostMapping
    public ResponseEntity<GuestDto> addGuest(@Valid @RequestBody GuestDto guestDto) {
        return ResponseEntity.ok(guestService.createNewGuest(guestDto));      // Persists new guest information
    }

    // Retrieves all guest records
    @GetMapping
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());                // Returns complete guest list
    }

    // Retrieves guest details by identifier
    @GetMapping("/{guestId}")
    public ResponseEntity<GuestDto> getGuestById(@PathVariable Long guestId) {
        return ResponseEntity.ok(guestService.getGuestById(guestId));         // Returns requested guest details
    }

    // Updates an existing guest record
    @PutMapping("/{guestId}")
    public ResponseEntity<GuestDto> updateGuestById(@PathVariable Long guestId, @Valid @RequestBody GuestDto guestDto) {
        return ResponseEntity.ok(guestService.updateGuestById(guestId, guestDto));    // Replaces guest information
    }

    // Updates selected guest fields
    @PatchMapping("/{guestId}")
    public ResponseEntity<GuestDto> partiallyUpdateGuestById(@PathVariable Long guestId, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(guestService.partiallyUpdateGuestById(guestId, updates));   // Applies partial modifications
    }

    // Removes a guest record
    @DeleteMapping("/{guestId}")
    public ResponseEntity<Void> deleteGuestById(@PathVariable Long guestId) {
        guestService.deleteGuestById(guestId);                                // Deletes guest from system
        return ResponseEntity.noContent().build();                            // Returns successful deletion response
    }
}

/*
    GuestController

        Purpose :
            Handles guest management APIs of the application.

        Responsibilities :
            - Create guest records
            - Retrieve guest information
            - Update guest details
            - Partially update guest details
            - Delete guest records

        Endpoints :

            POST /guests
                - Creates a new guest

            GET /guests
                - Retrieves all guests

            GET /guests/{guestId}
                - Retrieves guest by id

            PUT /guests/{guestId}
                - Updates complete guest information

            PATCH /guests/{guestId}
                - Updates selected guest fields

            DELETE /guests/{guestId}
                - Deletes a guest record

        Guest Management Flow :

            Create Guest
                    ↓
            Guest Stored
                    ↓
            Retrieve / Update
                    ↓
            Guest Information Maintained
                    ↓
            Delete When Required

        Update Flow :

            Guest Exists
                    ↓
            Receive Update Request
                    ↓
            Validate Data
                    ↓
            Apply Changes
                    ↓
            Return Updated Guest

        Business Use :
            - Guest registration
            - Guest profile management
            - Booking participant management
            - Customer information maintenance

        Security Features :
            - Request validation
            - Controlled data updates
            - Entity existence verification

        Note :
            - POST creates a new guest.
            - PUT replaces complete guest information.
            - PATCH updates only provided fields.
            - DELETE permanently removes guest data.

        This controller acts as the primary entry point
        for guest management operations.
*/