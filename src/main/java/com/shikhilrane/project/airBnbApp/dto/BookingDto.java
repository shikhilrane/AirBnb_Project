package com.shikhilrane.project.airBnbApp.dto;

import com.shikhilrane.project.airBnbApp.entity.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {
    private Long id;                          // Unique identifier of the booking

    private Integer roomsCount;               // Number of rooms booked

    private LocalDate checkInDate;            // Guest check-in date

    private LocalDate checkOutDate;           // Guest check-out date

    private LocalDateTime createdAt;          // Timestamp when booking was created

    private LocalDateTime updatedAt;          // Timestamp when booking was last updated

    private BookingStatus bookingStatus;      // Current booking status

    private Set<GuestDto> guests;             // Guests associated with this booking
}

/*
    BookingDto

        Purpose : Transfers booking information between
                  the server and client.

        This DTO contains :
            - Booking ID
            - Number of rooms booked
            - Check-in date
            - Check-out date
            - Booking status
            - Guest details
            - Creation timestamp
            - Last update timestamp

        Business Use :
            - Returns booking details to clients.
            - Used after booking creation.
            - Used when fetching booking information.
            - Avoids exposing Booking entity directly.

        Example Response :

            {
                "id": 1,
                "roomsCount": 2,
                "checkInDate": "2026-06-10",
                "checkOutDate": "2026-06-12",
                "bookingStatus": "RESERVED",
                "guests": [
                    {
                        "name": "ABC",
                        "age": 30
                    },
                    {
                        "name": "DEF",
                        "age": 32
                    }
                ]
            }

        Booking Flow :

            Booking Request
                    ↓
              Booking Entity
                    ↓
               BookingDto
                    ↓
               API Response

        Note :
            - This is not a database entity.
            - Used only for request/response data transfer.
            - Helps keep internal entity structure hidden.

        This DTO represents booking details returned to clients.
*/