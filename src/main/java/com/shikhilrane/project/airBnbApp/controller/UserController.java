package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.BookingDto;
import com.shikhilrane.project.airBnbApp.dto.ProfileUpdateRequestDto;
import com.shikhilrane.project.airBnbApp.dto.UserDto;
import com.shikhilrane.project.airBnbApp.service.BookingService;
import com.shikhilrane.project.airBnbApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    // Updates the profile information of the authenticated user
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        userService.updateProfile(profileUpdateRequestDto);                  // Updates user profile details
        return ResponseEntity.noContent().build();                          // Returns successful update response
    }

    // Retrieves all bookings of the authenticated user
    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDto>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());           // Returns booking history of current user
    }

    // Retrieves profile details of the authenticated user
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());               // Returns current user profile information
    }
}

/*
    UserController

        Purpose :
            Handles user profile and booking-related operations.

        Responsibilities :
            - Update user profile
            - Retrieve user profile
            - Retrieve user booking history

        Endpoints :

            PATCH /users/profile
                - Updates authenticated user's profile

            GET /users/profile
                - Retrieves authenticated user's profile

            GET /users/myBookings
                - Retrieves authenticated user's bookings

        Profile Management Flow :

            Authenticated User
                    ↓
            Profile Update Request
                    ↓
            User Service
                    ↓
            Profile Updated
                    ↓
            Success Response

        Profile Retrieval Flow :

            Authenticated User
                    ↓
            Profile Request
                    ↓
            User Service
                    ↓
            User Information
                    ↓
            API Response

        Booking History Flow :

            Authenticated User
                    ↓
            Booking History Request
                    ↓
            Booking Service
                    ↓
            User Bookings
                    ↓
            API Response

        Business Use :
            - User profile management
            - Personal information updates
            - Booking history tracking
            - Customer self-service operations

        Security Features :
            - Authenticated user access
            - User-specific data retrieval
            - Profile ownership validation
            - Controlled profile updates

        Note :
            - Profile operations are performed for the currently authenticated user.
            - Booking history contains only user-owned bookings.
            - Business logic is delegated to service layers.
            - Controller only handles HTTP requests and responses.

        This controller acts as the user
        account management entry point
        of the application.
*/