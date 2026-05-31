package com.shikhilrane.project.airBnbApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "unique_hotel_room_date",
                columnNames = {"hotel_id", "room_id", "date"}
        ))                                                              // Prevents duplicate inventory records for same hotel, room, and date
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                                    // Unique identifier for inventory record

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;                                                // Hotel to which this inventory belongs

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "room_id", nullable = false)
    private Room room;                                                  // Room type for which inventory is maintained

    @Column(nullable = false)
    private LocalDate date;                                             // Date for which inventory is maintained

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")   // Starts with default value as 0
    private Integer bookedCount;                                        // Number of rooms already booked

    @Column(nullable = false)
    private Integer totalCount;                                         // Total rooms available for booking

    @Column(nullable = false, precision = 5, scale = 2)                 // Can go up-to 999.99
    private BigDecimal surgeFactor;                                     // Dynamic pricing multiplier (e.g. 1.25x, 1.50x)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;                                           // Final room price after applying surge factor

    @Column(nullable = false)
    private String city;                                                // City used for faster search/filter operations

    @Column(nullable = false)
    private Boolean closed;                                             // Indicates whether booking is closed for this date

    @CreationTimestamp
    private LocalDateTime createdAt;                                    // Timestamp when inventory record was created

    @UpdateTimestamp
    private LocalDateTime updatedAt;                                    // Timestamp when inventory record was last updated
}

/*
    Inventory Entity

    Purpose : Stores day-wise room availability and pricing information.
              Tracks room inventory, bookings, and dynamic pricing
              for a specific hotel room on a specific date.

    This entity stores :
        - Hotel information
        - Room information
        - Inventory date
        - Number of booked rooms
        - Total available rooms
        - Surge pricing multiplier
        - Final room price
        - City information for faster search
        - Booking availability status
        - Audit information (createdAt, updatedAt)

    Relationships :
        - Many Inventory Records -> One Hotel
        - Many Inventory Records -> One Room

    Example :
        Inventory #101
            Hotel         : Taj Hotel
            Room          : Deluxe Room
            Date          : 25 Dec 2026
            Total Rooms   : 20
            Booked Rooms  : 15
            Available     : 5
            Surge Factor  : 1.50
            Final Price   : ₹7500
            Status        : Open

    Business Use :
        - Maintains room availability for each date
        - Prevents overbooking
        - Supports dynamic pricing based on demand
        - Powers hotel search and booking flows
        - Acts as the source of truth for room availability

    Note :
        - One inventory record exists for a specific
          Hotel + Room + Date combination.
        - Duplicate inventory records are prevented
          using a unique constraint.

    Example Unique Key :

        Taj Hotel + Deluxe Room + 25 Dec 2026

        ✓ Allowed Once
        ✗ Cannot Be Created Again

    Each record in the "inventory" table represents
    inventory and pricing information for a room on a specific date.
*/