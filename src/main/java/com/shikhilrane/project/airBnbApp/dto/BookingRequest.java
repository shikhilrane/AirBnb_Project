package com.shikhilrane.project.airBnbApp.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    @NotNull(message = "Hotel ID is required")
    @Positive(message = "Hotel ID must be greater than 0")
    private Long hotelId;                // ID of the hotel being booked

    @NotNull(message = "Room ID is required")
    @Positive(message = "Room ID must be greater than 0")
    private Long roomId;                 // ID of the room being booked

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date cannot be in the past")
    private LocalDate checkInDate;       // Guest check-in date

    @NotNull(message = "Check-out date is required")
    @FutureOrPresent(message = "Check-out date cannot be in the past")
    private LocalDate checkOutDate;      // Guest check-out date

    @NotNull(message = "Rooms count is required")
    @Positive(message = "Rooms count must be greater than 0")
    private Integer roomsCount;          // Number of rooms requested
}

/*
    BookingRequest

        Purpose : Receives booking details from the client
                  to initialize a new booking.

        This DTO contains :
            - Hotel ID
            - Room ID
            - Check-in date
            - Check-out date
            - Number of rooms required

        Validations :
            - Hotel ID must be greater than 0
            - Room ID must be greater than 0
            - Check-in date cannot be null
            - Check-in date cannot be in the past
            - Check-out date cannot be null
            - Check-out date cannot be in the past
            - Rooms count must be greater than 0

        Example Request :

            {
                "hotelId": 1,
                "roomId": 2,
                "checkInDate": "2026-06-10",
                "checkOutDate": "2026-06-12",
                "roomsCount": 2
            }

        Business Use :
            - Used when a guest creates a booking.
            - Validates booking details before processing.
            - Used for inventory checks and booking creation.

        Note :
            - This is not a database entity.
            - Used only for incoming API requests.

        This DTO represents the input required
        to initialize a hotel booking.
*/