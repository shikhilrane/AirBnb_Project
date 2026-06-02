package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.BookingDto;
import com.shikhilrane.project.airBnbApp.dto.BookingRequest;
import com.shikhilrane.project.airBnbApp.dto.GuestDto;
import com.shikhilrane.project.airBnbApp.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
@Validated
public class HotelBookingController {

    private final BookingService bookingService;

    // 1. Initialize a new booking
    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@Valid @RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    // 2. Add guests to an existing booking
    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId, @Valid @RequestBody List<GuestDto> guestDtoList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }
}

/*
    HotelBookingController

        Purpose : Handles hotel booking operations for guests.

        Responsibilities :
            - Create a new booking
            - Add guests to a booking

        Endpoints :

            POST /bookings/init
                - Initializes a booking
                - Reserves room inventory
                - Creates booking record

            POST /bookings/{bookingId}/addGuests
                - Adds guest details to a booking

        Flow :

            Guest Request
                  ↓
            Controller
                  ↓
            Service Layer
                  ↓
            Inventory Validation
                  ↓
            Booking Creation
                  ↓
            Database

        Booking Flow :

            Search Hotel
                  ↓
            Select Room
                  ↓
            Initialize Booking
                  ↓
            Add Guests
                  ↓
            Complete Booking

        Note :
            - Booking initialization checks room availability.
            - Inventory records are locked during booking creation.
            - Guest details are attached to an existing booking.
            - Controller contains no business logic.

        This controller is responsible for booking-related operations.
*/
