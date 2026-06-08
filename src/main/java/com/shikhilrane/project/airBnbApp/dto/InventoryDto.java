package com.shikhilrane.project.airBnbApp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryDto {
    private Long id;                        // Unique inventory record identifier
    private LocalDate date;                 // Inventory date
    private Integer bookedCount;            // Number of rooms already booked
    private Integer reservedCount;          // Number of rooms temporarily reserved
    private Integer totalCount;             // Total available room inventory
    private BigDecimal surgeFactor;         // Dynamic pricing multiplier
    private BigDecimal price;               // Effective room price for the date
    private Boolean closed;                 // Indicates whether booking is closed for the date
    private LocalDateTime createdAt;        // Inventory creation timestamp
    private LocalDateTime updatedAt;        // Last inventory update timestamp
}

/*
    InventoryDto

        Purpose :
            Represents room inventory information
            for a specific date.

        Responsibilities :
            - Transfer inventory details
            - Transfer room availability information
            - Transfer pricing information
            - Support inventory management operations

        Fields :

            id
                - Unique inventory identifier

            date
                - Date for which inventory is maintained

            bookedCount
                - Number of confirmed bookings

            reservedCount
                - Number of temporarily reserved rooms

            totalCount
                - Total room capacity available

            surgeFactor
                - Dynamic pricing multiplier

            price
                - Final room price for the date

            closed
                - Indicates booking availability status

            createdAt
                - Inventory creation timestamp

            updatedAt
                - Last modification timestamp

        Inventory Lifecycle :

            Inventory Created
                    ↓
            Room Availability Defined
                    ↓
            Reservations Added
                    ↓
            Bookings Confirmed
                    ↓
            Inventory Updated

        Availability Calculation :

            Total Inventory
                    ↓
            Reserved Rooms
                    ↓
            Booked Rooms
                    ↓
            Remaining Availability

        Business Use :
            - Room availability management
            - Dynamic pricing
            - Inventory tracking
            - Reservation management
            - Occupancy planning

        Note :
            - Inventory is maintained per date.
            - Pricing may vary based on surge factor.
            - Reserved inventory may later become booked.
            - Closed inventory cannot accept new bookings.
            - Business logic is handled outside the DTO.

        This DTO acts as the inventory
        representation model for room availability
        and pricing information.
*/