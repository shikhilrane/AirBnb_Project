package com.shikhilrane.project.airBnbApp.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddGuestsToBookingRequestDto {
    private List<Long> guestIds;      // Guest identifiers to be associated with a booking
}

/*
    AddGuestsToBookingRequestDto

        Purpose :
            Carries guest identifiers required to add guests
            to an existing booking.

        Responsibilities :
            - Accept guest IDs from client requests
            - Transfer guest data to service layer
            - Support booking guest association

        Fields :

            guestIds
                - List of guest identifiers
                - Represents guests to be linked with a booking

        Request Flow :

            Client Request
                    ↓
            Guest IDs Submitted
                    ↓
            DTO Mapping
                    ↓
            Service Layer
                    ↓
            Guests Added To Booking

        Business Use :
            - Booking guest management
            - Multi-guest reservations
            - Guest association workflow

        Note :
            - Contains only guest identifiers.
            - Actual guest validation is handled in the service layer.
            - Used during guest addition operations for bookings.

        This DTO acts as the request model
        for adding guests to an existing booking.
*/