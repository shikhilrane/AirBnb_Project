package com.shikhilrane.project.airBnbApp.entity;

import com.shikhilrane.project.airBnbApp.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Setter
@Getter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                        // Unique identifier for each booking

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;                                    // Hotel being booked

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "room_id", nullable = false)
    private Room room;                                      // Room type being booked

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                                      // User who made the booking

    @Column(nullable = false)
    private Integer roomsCount;                             // Number of rooms booked

    @Column(nullable = false)
    private LocalDate checkInDate;                          // Guest check-in date

    @Column(nullable = false)
    private LocalDate checkOutDate;                         // Guest check-out date

    @CreationTimestamp
    private LocalDateTime createdAt;                        // Timestamp when booking was created

    @UpdateTimestamp
    private LocalDateTime updatedAt;                        // Timestamp when booking was last updated

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;                                // Payment associated with this booking

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;                    // Current booking status (PENDING, CONFIRMED, CANCELLED, etc.)

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "booking_guest",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id")
    )
    private Set<Guest> guests;                              // Guests staying under this booking
}

/*
    Booking Entity

    Purpose : Represents a hotel room reservation made by a user.
              Stores booking details, stay dates, guests,
              payment information, and booking status.

    This entity stores :
        - Hotel being booked
        - Room being booked
        - User who made the booking
        - Number of rooms booked
        - Check-in and check-out dates
        - Payment information
        - Booking status
        - Guest details
        - Audit information (createdAt, updatedAt)

    Relationships :
        - Many Bookings -> One Hotel
        - Many Bookings -> One Room
        - Many Bookings -> One User
        - One Booking -> One Payment
        - Many Bookings <-> Many Guests

    Example :

        Booking #101

            User          : Rahul
            Hotel         : Taj Hotel
            Room          : Deluxe Room
            Rooms         : 2
            Check-In      : 10 Jan 2026
            Check-Out     : 12 Jan 2026
            Guests        : Rahul, Priya, Aman
            Payment       : ₹12,000
            Booking Status: CONFIRMED

    Business Use :
        - Represents a confirmed or pending reservation
        - Connects users with hotels and room types
        - Tracks stay duration and guest information
        - Maintains booking lifecycle and payment status
        - Acts as the primary record for hotel reservations

    Booking Lifecycle :

        PENDING
            ↓
        PAYMENT_PENDING
            ↓
        CONFIRMED
            ↓
        CHECKED_IN
            ↓
        CHECKED_OUT

           OR

        PENDING
            ↓
        CANCELLED

    Note :
        - One booking can contain multiple guests.
        - One booking is associated with a single hotel and room type.
        - Availability is validated using Inventory records before booking creation.

    Each record in the "booking" table represents one hotel reservation.
*/