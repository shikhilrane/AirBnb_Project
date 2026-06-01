package com.shikhilrane.project.airBnbApp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomDto {
    @Positive(message = "Id must be greater than 0")                           // Validates that ID is greater than 0
    private Long id;                                                           // Unique identifier of the room

    @NotBlank(message = "Room type is required")                               // Prevents null, empty, or blank room type
    @Size(min = 2, max = 50, message = "Room type must be between 2 and 50 characters")
    private String type;                                                       // Room type (Deluxe, Suite, Standard, etc.)

    @NotNull(message = "Base price is required")                               // Prevents null price
    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    private BigDecimal basePrice;                                              // Base room price per night

    @NotEmpty(message = "At least one room photo is required")                 // Ensures at least one room photo is provided
    private String[] photos;                                                   // Room photo URLs

    @NotEmpty(message = "At least one amenity is required")                    // Ensures at least one room amenity is provided
    private String[] amenities;                                                // Room amenities

    @NotNull(message = "Total room count is required")                         // Prevents null total room count
    @Positive(message = "Total room count must be greater than 0")
    private Integer totalCount;                                                // Total rooms available of this type

    @NotNull(message = "Capacity is required")                                 // Prevents null capacity
    @Positive(message = "Capacity must be greater than 0")
    private Integer capacity;                                                  // Maximum guests allowed
}


/*
    RoomDto

        Purpose : Acts as a Data Transfer Object for room-related requests and responses.

        This DTO contains :
            - Room ID
            - Room type
            - Base room price
            - Room photos
            - Room amenities
            - Total room count
            - Guest capacity

        Validations :
            - ID must be greater than 0
            - Room type cannot be blank
            - Room type length must be between 2 and 50 characters
            - Base price must be greater than 0
            - At least one photo is required
            - At least one amenity is required
            - Total room count must be greater than 0
            - Capacity must be greater than 0

        Example Request :

            {
                "type": "Deluxe Room",
                "basePrice": 5000,
                "photos": [
                    "http://image1.com"
                ],
                "amenities": [
                    "WiFi",
                    "AC",
                    "TV"
                ],
                "totalCount": 20,
                "capacity": 3
            }

        Business Use :
            - Receives room data from API requests
            - Sends room data in API responses
            - Validates room information before processing
            - Prevents invalid room configurations

        Note :
            - Each RoomDto represents a room category/type.
            - Example: Deluxe Room, Suite Room, Standard Room.
*/