package com.shikhilrane.project.airBnbApp.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;        // Unique identifier of the user
    private String email;   // User email address
    private String name;    // User full name
}

/*
    UserDto

        Purpose : Transfers user information between layers.

        Responsibilities :
            - Return user details
            - Hide sensitive information
            - Act as response object

        Fields :

            id
                - Unique user identifier

            email
                - User email address

            name
                - User full name

        Example :

            {
                "id": 1,
                "email": "rahul@gmail.com",
                "name": "Rahul Sharma"
            }

        Business Use :
            - User registration response
            - User profile response
            - User information display
            - Authentication response

        Security Benefits :
            - Hides password field
            - Hides internal security data
            - Exposes only required information

        Flow :

            User Entity
                    ↓
                UserDto
                    ↓
            API Response
                    ↓
                Client

        Example Usage :

            Signup
                ↓
            User Created
                ↓
            UserDto Returned

        Note :
            - Does not contain password.
            - Used for API responses.
            - Prevents exposing sensitive user data.

        This DTO acts as the user response object
        of the application.
*/