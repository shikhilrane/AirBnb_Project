package com.shikhilrane.project.airBnbApp.entity;

import com.shikhilrane.project.airBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                    // Unique identifier for each guest

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;                                  // User who owns/saved this guest

    @Column(nullable = false)
    private String name;                                // Full name of the guest

    @Enumerated(EnumType.STRING)
    private Gender gender;                              // Gender of the guest from Gender enum

    private Integer age;                                // Age of the guest
}

/*
    Guest Entity

    Purpose : Represents a guest staying at a hotel under a booking.
              Stores personal information of guests who will be
              checking in and staying at the property.

    This entity stores :
        - Guest name
        - Gender
        - Age
        - User who added the guest

    Relationships :
        - Many Guests -> One User
        - Many Guests <-> Many Bookings

    Example :
        Guest #1
            Name    : Priya Sharma
            Gender  : FEMALE
            Age     : 28
            Added By: Rahul Sharma

    Business Use :
        - Allows users to save frequent travelers.
        - Supports bookings with multiple guests.
        - Maintains guest information required during check-in.
        - Eliminates the need to enter guest details repeatedly.

    Example Scenario :

        User : Rahul

        Saved Guests :

            Guest #1 -> Rahul Sharma
            Guest #2 -> Priya Sharma
            Guest #3 -> Aman Sharma

        Booking #101

            Guests :
                - Rahul Sharma
                - Priya Sharma
                - Aman Sharma

    Note :
        - A user can save multiple guests.
        - The same guest can be used in multiple bookings.
        - Guests are linked to bookings through the booking_guest table.

    Each record in the "guest" table represents one traveler/guest.
*/