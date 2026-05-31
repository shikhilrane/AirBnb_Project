package com.shikhilrane.project.airBnbApp.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HotelContactInfo {
    private String address;     // Stores hotel address
    private String phoneNumber; // Stores hotel contact number
    private String email;       // Stores hotel email address
    private String location;    // Stores hotel location information or coordinates
}

/*
   HotelContactInfo
       Purpose : Stores contact-related information of a hotel.

       This is an embedded object, meaning :
           - No separate table will be created.
           - Its fields will be stored inside the hotel table.

       Reusable contact information component.
*/