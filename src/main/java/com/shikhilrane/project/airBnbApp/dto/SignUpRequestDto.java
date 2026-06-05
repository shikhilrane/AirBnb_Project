package com.shikhilrane.project.airBnbApp.dto;

import lombok.Data;

@Data
public class SignUpRequestDto {
    private String email;    // User email address
    private String password; // User password
    private String name;     // User full name
}

/*
    SignUpRequestDto

        Purpose : Stores user registration details.

        Responsibilities :
            - Capture user email
            - Capture user password
            - Capture user name
            - Transfer signup data from client to server

        Fields :

            email
                - User email address

            password
                - User password

            name
                - User full name

        Signup Flow :

            User Registration Request
                    ↓
            SignUpRequestDto
                    ↓
            AuthController
                    ↓
            AuthService
                    ↓
            User Created

        Example Request :

            {
                "email": "rahul@gmail.com",
                "password": "password123",
                "name": "Rahul Sharma"
            }

        Business Use :
            - User registration
            - Account creation
            - Authentication setup
            - User onboarding

        Security Flow :

            Password Entered
                    ↓
            SignUpRequestDto
                    ↓
            PasswordEncoder
                    ↓
            Encrypted Password
                    ↓
            Database

        Note :
            - Contains plain password before encryption.
            - Used only during signup.
            - Converted into User entity before saving.

        This DTO acts as the signup request object
        of the application.
*/