package com.shikhilrane.project.airBnbApp.dto;

import com.shikhilrane.project.airBnbApp.entity.HotelContactInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HotelDto {
    @Positive(message = "Id must be greater than 0")                                        // Validates that ID is greater than 0
    private Long id;                                                                        // Unique identifier of the hotel

    @NotBlank(message = "Hotel name is required")                                           // Prevents null, empty, or blank hotel names
    @Size(min = 2, max = 100, message = "Hotel name must be between 2 and 100 characters")  // Restricts hotel name length
    private String name;                                                                    // Hotel name

    @NotBlank(message = "City is required")                                                 // Prevents null, empty, or blank city values
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")          // Restricts city name length
    private String city;                                                                    // City where hotel is located

    @NotEmpty(message = "At least one hotel photo is required")                             // Ensures at least one photo is provided
    private String[] photos;                                                                // Hotel photo URLs

    @NotEmpty(message = "At least one amenity is required")                                 // Ensures at least one amenity is provided
    private String[] amenities;                                                             // Hotel amenities such as WiFi, Pool, Parking

    @Valid                                                                                  // Triggers validation for nested HotelContactInfo object
    @NotNull(message = "Contact information is required")                                   // Prevents null contact information
    private HotelContactInfo contactInfo;                                                   // Hotel contact details

    private Boolean active;                                                                 // Indicates whether hotel is active or inactive
}

/*
    HotelDto

        Purpose : Acts as a Data Transfer Object for hotel-related requests and responses.
                  Used to transfer hotel data between the client and server.

        This DTO contains :
            - Hotel ID
            - Hotel name
            - City
            - Hotel photos
            - Hotel amenities
            - Contact information
            - Active/Inactive status

        Validations :
            - ID must be greater than 0
            - Name cannot be blank
            - Name length must be between 2 and 100 characters
            - City cannot be blank
            - City length must be between 2 and 50 characters
            - At least one photo is required
            - At least one amenity is required
            - Contact information cannot be null
            - Active status cannot be null

        Example Request :

            {
                "name": "Hotel Lotus",
                "city": "Pune",
                "photos": ["http://image1.com"],
                "amenities": ["WiFi", "Pool"],
                "contactInfo": {
                    "address": "Wakad",
                    "phoneNumber": "9876543210",
                    "email": "hotel@gmail.com",
                    "location": "18.5204,73.8567"
                },
                "active": true
            }

        Business Use :
            - Receives hotel data from API requests
            - Sends hotel data in API responses
            - Prevents exposing entity objects directly to clients
            - Applies validation before data reaches the service layer

        Note :
            - This is not a database table.
            - It is only used for data transfer between layers.

        Flow :

            Client
               ↓
            HotelDto
               ↓
            Controller
               ↓
            Service
               ↓
            Entity
               ↓
            Database
*/