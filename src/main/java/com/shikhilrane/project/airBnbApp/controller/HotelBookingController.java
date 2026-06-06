package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.BookingDto;
import com.shikhilrane.project.airBnbApp.dto.BookingRequest;
import com.shikhilrane.project.airBnbApp.dto.BookingStatusResponseDto;
import com.shikhilrane.project.airBnbApp.dto.GuestDto;
import com.shikhilrane.project.airBnbApp.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
@Validated
public class HotelBookingController {

    private final BookingService bookingService;        // Handles booking, guest, payment and cancellation operations

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

    // 3. Creates Stripe checkout session and returns payment URL
    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long bookingId) {
        String sessionUrl = bookingService.initiatePayments(bookingId);             // Generates Stripe checkout session
        return ResponseEntity.ok(Map.of("sessionUrl", sessionUrl));
    }

    // 4. Cancels an existing booking and triggers refund if applicable
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);                                    // Cancels booking and releases inventory
        return ResponseEntity.noContent().build();
    }

    // 5. Returns current booking status
    @GetMapping("/{bookingId}/status")
    public ResponseEntity<BookingStatusResponseDto> getBookingStatus(@PathVariable Long bookingId) {
        return ResponseEntity.ok(new BookingStatusResponseDto(bookingService.getBookingStatus(bookingId)));
    }
}

/*
    HotelBookingController

        Purpose :
            Handles hotel booking operations for guests.
            Acts as the entry point for booking lifecycle management.

        Responsibilities :
            - Create bookings
            - Add guests to bookings
            - Initiate payments
            - Cancel bookings
            - Fetch booking status

        Endpoints :

            POST /bookings/init
                - Creates a booking
                - Reserves room inventory
                - Calculates booking amount

            POST /bookings/{bookingId}/addGuests
                - Adds guest information
                - Associates guests with booking

            POST /bookings/{bookingId}/payments
                - Creates Stripe checkout session
                - Returns payment URL

            POST /bookings/{bookingId}/cancel
                - Cancels booking
                - Releases inventory
                - Initiates refund if payment exists

            GET /bookings/{bookingId}/status
                - Returns current booking status

        Booking Lifecycle :

            Booking Created
                    ↓
                RESERVED
                    ↓
            Add Guest Details
                    ↓
             Initiate Payment
                    ↓
            PAYMENTS_PENDING
                    ↓
            Payment Success
                    ↓
                CONFIRMED

        Cancellation Flow :

            Confirmed Booking
                    ↓
              Cancel Request
                    ↓
             Inventory Released
                    ↓
             Refund Initiated
                    ↓
                CANCELLED

        Payment Flow :

            Booking Created
                    ↓
            Payment Endpoint
                    ↓
          Stripe Checkout URL
                    ↓
           User Completes Payment
                    ↓
              Stripe Webhook
                    ↓
             Booking Confirmed

        Status Flow :

            Client Request
                    ↓
           Get Booking Status
                    ↓
            Service Layer
                    ↓
            Current Status
                    ↓
             API Response

        Business Use :
            - Room reservation
            - Guest management
            - Payment processing
            - Booking tracking
            - Booking cancellation
            - Refund handling

        Security Features :
            - Authenticated booking access
            - Booking ownership validation
            - Inventory locking
            - Secure payment processing
            - Stripe webhook integration

        Note :
            - Inventory is reserved during booking initialization.
            - Payment is handled through Stripe Checkout.
            - Booking confirmation occurs through webhooks.
            - Refunds are triggered during cancellation.
            - Controller contains no business logic.

        This controller acts as the booking
        management entry point of the application.
*/
