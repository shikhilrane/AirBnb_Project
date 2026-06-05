package com.shikhilrane.project.airBnbApp.security.impl;

import com.shikhilrane.project.airBnbApp.dto.LoginDto;
import com.shikhilrane.project.airBnbApp.dto.SignUpRequestDto;
import com.shikhilrane.project.airBnbApp.dto.UserDto;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.entity.enums.Role;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.repository.UserRepository;
import com.shikhilrane.project.airBnbApp.security.AuthService;
import com.shikhilrane.project.airBnbApp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;                // Performs user database operations
    private final ModelMapper modelMapper;                      // Converts DTO objects into Entity objects and vice versa
    private final PasswordEncoder passwordEncoder;              // Encrypts user passwords before saving
    private final AuthenticationManager authenticationManager;  // Authenticates user credentials during login
    private final JwtService jwtService;                        // Generates and validates JWT tokens

    // Registers a new user account
    @Override
    public UserDto signUp(SignUpRequestDto signUpRequestDto) {
        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);   // Checks whether email already exists

        if (user != null) {
            throw new RuntimeException("User is already present with same email id");       // Prevents duplicate user registration
        }

        User newUser = modelMapper.map(signUpRequestDto, User.class);                       // Converts signup request into User entity
        newUser.setRoles(Set.of(Role.GUEST));                                               // Assigns default GUEST role
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));        // Encrypts password before saving
        newUser = userRepository.save(newUser);                                             // Saves user in database

        return modelMapper.map(newUser, UserDto.class);                                     // Converts saved entity into DTO response
    }

    // Authenticates user and generates JWT tokens
    @Override
    public String[] login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()             // Verifies email and password
        ));

        User user = (User) authentication.getPrincipal();               // Gets authenticated user

        String[] arr = new String[2];
        arr[0] = jwtService.generateAccessToken(user);                  // Generates access token
        arr[1] = jwtService.generateRefreshToken(user);                 // Generates refresh token

        return arr;                                                     // Returns access token and refresh token
    }

    // Generates new access token using refresh token
    @Override
    public String refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);          // Extracts user ID from refresh token

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id));    // Loads user from database
        return jwtService.generateAccessToken(user);                    // Generates new access token
    }
}

/*
    AuthServiceImpl

        Purpose : Handles authentication and authorization operations.

        Responsibilities :
            - User signup
            - User login
            - Access token generation
            - Refresh token generation
            - Access token renewal

        Methods :

            signUp()
                - Registers new user
                - Encrypts password
                - Assigns default role
                - Saves user in database

            login()
                - Authenticates user
                - Generates access token
                - Generates refresh token

            refreshToken()
                - Validates refresh token
                - Generates new access token

        Authentication Flow :

            Signup
                ↓
            User Created
                ↓
            Login
                ↓
            Email + Password Verification
                ↓
            Access Token
                    +
            Refresh Token

        Refresh Flow :

            Access Token Expired
                    ↓
            Refresh Token
                    ↓
            User Loaded
                    ↓
            New Access Token

        Business Use :
            - User registration
            - User authentication
            - JWT token generation
            - Session renewal
            - Secure login flow

        Security Features :
            - Password encryption using BCrypt
            - JWT authentication
            - Access token expiration
            - Refresh token support

        Note :
            - Every new user gets GUEST role.
            - Passwords are stored in encrypted form.
            - Refresh token is used to generate new access tokens.
            - Authentication is delegated to Spring Security.

        This class acts as the authentication service of the application.
*/