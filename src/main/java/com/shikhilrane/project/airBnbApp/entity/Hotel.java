package com.shikhilrane.project.airBnbApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // Unique identifier for each hotel

    @Column(nullable = false)
    private String name;                    // Stores hotel name

    private String city;                    // Stores city where hotel is located

    @Column(columnDefinition = "TEXT[]")    // Maps to PostgreSQL text array column
    private String[] photos;                // Stores hotel photo URLs

    @Column(columnDefinition = "TEXT[]")    // Maps to PostgreSQL text array column
    private String[] amenities;             // Stores hotel facilities like WiFi, Pool, etc.

    @CreationTimestamp
    private LocalDateTime createdAt;        // Stores creation timestamp

    @UpdateTimestamp
    private LocalDateTime updatedAt;        // Stores last update timestamp

    @Embedded                               // Embeds fields of HotelContactInfo into this table
    private HotelContactInfo contactInfo;   // Stores hotel contact details

    @Column(nullable = false)
    private Boolean active;                 // This will signify that if hotel is active or not
}

/*
    Hotel Entity

    Purpose : Represents a hotel listed on the Airbnb platform.
              Stores hotel details, amenities, photos, contact information,
              and availability status.

    This entity stores :
        - Hotel name
        - City
        - Hotel photos
        - Available amenities
        - Contact information
        - Active/Inactive status
        - Audit information (createdAt, updatedAt)

    Relationships :
        - One Hotel -> Many Rooms
        - One Hotel -> Many Inventories
        - One Hotel -> Many Bookings

    Example :
        Hotel #1
            Name        : Taj Hotel
            City        : Mumbai
            Amenities   : WiFi, Pool, Spa, Parking
            Contact     : +91-9876543210
            Status      : Active

    Business Use :
        - Hotels can be searched by city
        - Hotels expose available room types
        - Hotels receive bookings through rooms
        - Inactive hotels are hidden from customers

    Each record in the "hotel" table represents one hotel property.
*/