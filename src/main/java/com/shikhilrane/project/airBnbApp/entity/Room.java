package com.shikhilrane.project.airBnbApp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                        // Unique identifier for each room type

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonBackReference                                      // Child side of bidirectional relationship, ignored during JSON serialization to prevent infinite recursion
    private Hotel hotel;                                    // Hotel to which this room belongs

    @Column(nullable = false)
    private String type;                                    // Room type (Standard, Deluxe, Suite, etc.)

    @Column(nullable = false, precision = 10, scale = 2)    // Use precision when we use BigDecimal (Can go up-to 99999999.99)
    private BigDecimal basePrice;                           // Base price per night for this room

    @Column(columnDefinition = "TEXT[]")
    private String[] photos;                                // Photos of the room

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;                             // Amenities available in the room

    @Column(nullable = false)
    private Integer totalCount;                             // Total number of rooms available of this type

    @Column(nullable = false)
    private Integer capacity;                               // Maximum number of guests allowed

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;                        // Timestamp when room record was created

    @UpdateTimestamp
    private LocalDateTime updatedAt;                        // Timestamp when room record was last updated
}

/*
    Room Entity

    Purpose : Represents a room type/category within a hotel.
              Stores room details, pricing, amenities, capacity,
              and the total number of rooms available.

    This entity stores :
        - Hotel to which the room belongs
        - Room type (Standard, Deluxe, Suite, etc.)
        - Base price per night
        - Room photos
        - Room amenities
        - Total rooms available
        - Guest capacity
        - Audit information (createdAt, updatedAt)

    Relationships :
        - Many Rooms -> One Hotel
        - One Room -> Many Inventories
        - One Room -> Many Bookings

    Example :
        Room #1
            Hotel       : Taj Hotel
            Type        : Deluxe Room
            Base Price  : ₹5000
            Capacity    : 3 Guests
            Total Rooms : 20
            Amenities   : WiFi, AC, TV, Mini Bar

    Business Use :
        - Defines different room categories offered by a hotel
        - Acts as the base configuration for inventory generation
        - Provides pricing and capacity information during booking
        - Used to calculate availability and booking costs

    Note :
        - Each record represents a room type, not an individual physical room.
        - Example: A hotel may have 20 Deluxe Rooms, represented by a single Room record.

    Each record in the "room" table represents one room category.
*/