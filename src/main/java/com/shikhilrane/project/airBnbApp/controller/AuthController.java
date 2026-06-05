package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.LoginDto;
import com.shikhilrane.project.airBnbApp.dto.LoginResponseDto;
import com.shikhilrane.project.airBnbApp.dto.SignUpRequestDto;
import com.shikhilrane.project.airBnbApp.dto.UserDto;
import com.shikhilrane.project.airBnbApp.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Registers a new user account
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        return new ResponseEntity<>(authService.signUp(signUpRequestDto), HttpStatus.CREATED);
    }

    // Authenticates user and returns access token
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String[] tokens = authService.login(loginDto);                  // Generates access and refresh tokens

        Cookie cookie = new Cookie("refreshToken", tokens[1]);   // Creates refresh token cookie
        cookie.setHttpOnly(true);                                      // Prevents JavaScript access to cookie

        httpServletResponse.addCookie(cookie);                         // Adds refresh token cookie to response
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));     // Returns access token in response body
    }

    // Generates a new access token using refresh token
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(request.getCookies()).
                filter(cookie -> "refreshToken".equals(cookie.getName()))                                        // Finds refresh token cookie
                .findFirst()
                .map(Cookie::getValue)                                                                                  // Extracts refresh token value
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));   // Throws exception if cookie is missing

        String accessToken = authService.refreshToken(refreshToken);    // Generates new access token
        return ResponseEntity.ok(new LoginResponseDto(accessToken));    // Returns newly generated access token
    }
}

/*
    AuthController

        Purpose : Handles authentication APIs of the application.

        Responsibilities :
            - User signup
            - User login
            - Access token refresh

        Endpoints :

            POST /auth/signup
                - Registers a new user

            POST /auth/login
                - Authenticates user
                - Returns access token
                - Stores refresh token in cookie

            POST /auth/refresh
                - Generates new access token
                - Uses refresh token cookie

        Authentication Flow :

            Signup
                ↓
            User Created
                ↓
            Login
                ↓
            Access Token
                    +
            Refresh Token
                ↓
            Authenticated Requests

        Refresh Token Flow :

            Access Token Expired
                    ↓
            Refresh Endpoint
                    ↓
            Refresh Token Validation
                    ↓
            New Access Token

        Business Use :
            - User registration
            - User authentication
            - JWT token generation
            - Session renewal without login

        Security Features :
            - Password encryption
            - JWT authentication
            - HttpOnly refresh token cookie
            - Stateless authentication

        Note :
            - Access token is returned in response body.
            - Refresh token is stored in cookie.
            - Refresh token is used to generate new access tokens.

        This controller acts as the authentication entry point
        of the application.
*/