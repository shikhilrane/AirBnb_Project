package com.shikhilrane.project.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken; // JWT access token returned after successful authentication
}

/*
    LoginResponseDto

        Purpose : Stores access token returned after successful login.

        Responsibilities :
            - Return JWT access token
            - Provide authentication token to client

        Fields :

            accessToken
                - JWT token used for authenticated requests

        Authentication Flow :

            Login Request
                    ↓
            Credentials Verified
                    ↓
            Access Token Generated
                    ↓
            LoginResponseDto
                    ↓
            Client Receives Token

        Example Response :

            {
                "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
            }

        Business Use :
            - User authentication
            - Secure API access
            - JWT-based authorization
            - Access protected endpoints

        Token Usage :

            Authorization :
                Bearer <accessToken>

        Example :

            Authorization:
                Bearer eyJhbGciOiJIUzI1NiJ9...

        Note :
            - Contains only access token.
            - Refresh token is stored in HttpOnly cookie.
            - Access token is required for secured APIs.

        This DTO acts as the login response object
        of the application.
*/