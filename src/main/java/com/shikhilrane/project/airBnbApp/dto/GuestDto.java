package com.shikhilrane.project.airBnbApp.dto;

import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GuestDto {
    @Positive(message = "Guest ID must be greater than 0")
    private Long id;                                  // Unique guest identifier

    private User user;                               // Registered user linked to guest (optional)

    @NotBlank(message = "Guest name is required")
    @Size(min = 2, max = 50, message = "Guest name must be between 2 and 50 characters")
    private String name;                             // Guest full name

    @NotNull(message = "Gender is required")
    private Gender gender;                           // Guest gender

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age cannot exceed 120")
    private Integer age;                             // Guest age
}

/*
    GuestDto

        Purpose : Transfers guest information between
                  the client and server.

        This DTO contains :
            - Guest ID
            - Linked user (optional)
            - Guest name
            - Gender
            - Age

        Validations :
            - Guest ID must be greater than 0
            - Name cannot be blank
            - Name length must be between 2 and 50 characters
            - Gender cannot be null
            - Age must be between 1 and 120

        Example Request :

            {
                "name": "Rahul Sharma",
                "gender": "MALE",
                "age": 30
            }

        Business Use :
            - Used while adding guests to a booking.
            - Stores guest details for hotel stay.
            - Supports multiple guests under one booking.

        Flow :

            Guest Details
                  ↓
               GuestDto
                  ↓
              Booking
                  ↓
              Database

        Note :
            - This is not a database entity.
            - Used only for request/response data transfer.
            - One booking can have multiple guests.

        This DTO represents guest information
        associated with a booking.
*/