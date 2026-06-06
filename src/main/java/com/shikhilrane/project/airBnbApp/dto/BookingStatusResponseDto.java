package com.shikhilrane.project.airBnbApp.dto;

import com.shikhilrane.project.airBnbApp.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatusResponseDto {

    private BookingStatus bookingStatus;    // Current status of the booking
}

/*
    BookingStatusResponseDto

        Purpose :
            Returns the current status of a booking.

        Responsibilities :
            - Transfer booking status to client
            - Hide internal booking details
            - Act as booking status response object

        Field :

            bookingStatus
                - Current booking state

        Booking Status Flow :

            Booking Created
                    ↓
                RESERVED
                    ↓
            Payment Initiated
                    ↓
            PAYMENTS_PENDING
                    ↓
            Payment Success
                    ↓
                CONFIRMED

            OR

                CONFIRMED
                    ↓
                CANCELLED

        Example Response :

            {
                "bookingStatus": "CONFIRMED"
            }

        Possible Status Values :

            RESERVED
                - Inventory reserved
                - Payment not initiated

            PAYMENTS_PENDING
                - Payment session created
                - Awaiting payment completion

            CONFIRMED
                - Payment successful
                - Booking completed

            CANCELLED
                - Booking cancelled
                - Inventory released

        Business Use :
            - Booking tracking
            - Payment tracking
            - Booking confirmation checks
            - Booking status monitoring

        API Usage :

            GET /bookings/{bookingId}/status

                    ↓

            BookingStatusResponseDto

                    ↓

            Client Receives Status

        Note :
            - Contains only booking status.
            - Used for status polling.
            - Lightweight response object.
            - Does not expose booking details.

        This DTO acts as the booking
        status response object of the application.
*/