package com.shikhilrane.project.airBnbApp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateInventoryRequestDto {
    private LocalDate startDate;        // Inventory update start date
    private LocalDate endDate;          // Inventory update end date
    private BigDecimal surgeFactor;     // Dynamic pricing multiplier to apply
    private Boolean closed;             // Indicates whether booking should be closed
}

/*
    UpdateInventoryRequestDto

        Purpose :
            Carries inventory update information
            for a room across a date range.

        Responsibilities :
            - Transfer inventory update parameters
            - Support bulk inventory updates
            - Support pricing adjustments
            - Support inventory closure operations

        Fields :

            startDate
                - Beginning date of inventory update

            endDate
                - Ending date of inventory update

            surgeFactor
                - Dynamic pricing multiplier
                - Used to increase or decrease room pricing

            closed
                - Booking availability flag
                - Controls whether bookings are allowed

        Inventory Update Flow :

            Hotel Manager
                    ↓
            Select Date Range
                    ↓
            Define Inventory Changes
                    ↓
            UpdateInventoryRequestDto
                    ↓
            Service Layer
                    ↓
            Inventory Updated

        Pricing Flow :

            Base Room Price
                    ↓
            Apply Surge Factor
                    ↓
            Updated Room Price
                    ↓
            Available For Search

        Availability Flow :

            Inventory Record
                    ↓
            closed = false
                    ↓
            Accept Bookings

            OR

            Inventory Record
                    ↓
            closed = true
                    ↓
            Reject New Bookings

        Business Use :
            - Inventory management
            - Dynamic pricing
            - Seasonal pricing updates
            - Maintenance scheduling
            - Room availability control

        Note :
            - Updates are applied within the specified date range.
            - Surge factor affects room pricing calculations.
            - Closed inventory cannot accept new bookings.
            - Validation and business rules are handled in the service layer.
            - DTO contains only inventory update data.

        This DTO acts as the request model
        for inventory and pricing update operations.
*/