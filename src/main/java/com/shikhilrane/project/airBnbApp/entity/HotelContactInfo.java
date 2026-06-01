package com.shikhilrane.project.airBnbApp.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HotelContactInfo {
    @NotBlank(message = "Address is required")
    private String address;     // Stores hotel address

    @NotBlank(message = "Phone number is required")
    private String phoneNumber; // Stores hotel contact number

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;       // Stores hotel email address

    @NotBlank(message = "Location is required")
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