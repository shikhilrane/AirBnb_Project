package com.shikhilrane.project.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String email;       // User email used for authentication
    private String password;    // User password used for authentication
}

/*
    LoginDto

        Purpose : Stores user login credentials.

        Responsibilities :
            - Capture user email
            - Capture user password
            - Transfer login data from client to server

        Fields :

            email
                - User email address

            password
                - User password

        Authentication Flow :

            User Login Request
                    ↓
                LoginDto
                    ↓
            AuthController
                    ↓
            AuthService
                    ↓
            AuthenticationManager
                    ↓
            Login Success / Failure

        Example Request :

            {
                "email": "rahul@gmail.com",
                "password": "password123"
            }

        Business Use :
            - User login
            - JWT authentication
            - Access token generation
            - Refresh token generation

        Note :
            - Used only during login.
            - Contains plain password before encryption check.
            - Passed from client to authentication service.

        This DTO acts as the login request object
        of the application.
*/