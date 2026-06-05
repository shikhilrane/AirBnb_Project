package com.shikhilrane.project.airBnbApp.security.impl;

import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;    // Create a Minimum 256 bits (32 characters) secret key for HS256 in application.properties / env variable.

    // Creates secret key used for signing and validating JWT tokens
    private SecretKey getSecretKey(){   // Encode secret key with HS256 algorithm
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Generates access token containing user information and roles
    @Override
    public String generateAccessToken(User user) {
        return Jwts.builder()                                   // builder() to create the token
                .subject(user.getId().toString())               // subject would be our id (converted to string because it was in Long)
                .claim("email", user.getEmail())             // We can add as many as claim, claims as per our entity class
                .claim("roles", user.getRoles().toString())  // Accessed the roles for enums
                .issuedAt(new Date())                           // Issued as current time
                .expiration(new Date(System.currentTimeMillis() + 1000*60*10)) // Expire token from current time to 60 seconds * 10  (i.e. after 10 minute)
                .signWith(getSecretKey())                       // Sign a secret key
                .compact();                                     // Create a Token
    }

    // Generates long-lived refresh token
    @Override
    public String generateRefreshToken(User user) {
        return Jwts.builder()                                   // builder() to create the token
                .subject(user.getId().toString())               // subject would be our id (converted to string because it was in Long)
                .issuedAt(new Date())                           // Issued as current time
                .expiration(new Date(System.currentTimeMillis() + (1000L *60*60*24*30*6))) // Expire Refresh token from current time to 6 months (i.e. after 6 Months)
                .signWith(getSecretKey())                       // Sign a secret key
                .compact();                                     // Create a Token
    }

    // Extracts user ID from JWT token after validation
    @Override
    public Long getUserIdFromToken(String token) {          // Method to get user id from generated token
        Claims claims = Jwts.parser()                       // .parser() to get data from token
                .verifyWith(getSecretKey())                 // We are telling to verify this token from this secret key
                .build()
                .parseSignedClaims(token)                   // pass only the JWT token created with generateUserToken()
                .getPayload();                              // it includes subject, claims, issuedAt, expiration from token
        return Long.valueOf(claims.getSubject());
    }
}

/*
    JwtServiceImpl

        Purpose : Handles JWT token generation and validation.

        Responsibilities :
            - Generate access token
            - Generate refresh token
            - Extract user ID from token
            - Sign and verify JWT tokens

        Methods :

            getSecretKey()
                - Converts secret key string
                  into SecretKey object

            generateAccessToken()
                - Generates JWT access token
                - Stores user ID, email and roles
                - Valid for 10 minutes

            generateRefreshToken()
                - Generates JWT refresh token
                - Stores user ID
                - Valid for 6 months

            getUserIdFromToken()
                - Validates JWT token
                - Extracts user ID from token

        Authentication Flow :

            User Login
                    ↓
            generateAccessToken()
                    ↓
            Access Token

            generateRefreshToken()
                    ↓
            Refresh Token

        Authorization Flow :

            JWT Token
                    ↓
            getUserIdFromToken()
                    ↓
            User Loaded
                    ↓
            Access Granted

        Example Access Token Claims :

            Subject : 1
            Email   : abc@gmail.com
            Roles   : [HOTEL_MANAGER]

        Security Features :

            - HS256 Signature
            - Secret Key Verification
            - Token Expiration
            - Tamper Protection

        Token Lifecycle :

            Login
                ↓
            Access Token (10 min)
                ↓
            Expired
                ↓
            Refresh Token
                ↓
            New Access Token

        Note :
            - Access token contains user details.
            - Refresh token contains only user ID.
            - Secret key must be at least 256 bits.
            - Invalid tokens throw JWT exceptions.

        This class acts as the JWT authentication service
        of the application.
*/