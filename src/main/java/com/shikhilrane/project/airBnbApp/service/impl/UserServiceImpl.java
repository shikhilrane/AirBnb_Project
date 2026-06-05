package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.repository.UserRepository;
import com.shikhilrane.project.airBnbApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // Fetches user details using user ID
    @Override
    public User getUserById(Long id) {
        return userRepository
                .findById(id)                                                                               // Searches user by ID
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));     // Throws exception if user not found

    }

    // Loads user details using email for authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username)                                                                                  // Searches user by email
                .orElseThrow(() -> new BadCredentialsException("User with entered email " + username + " not found"));  // Throws exception if email not found

    }
}

/*
    UserServiceImpl

        Purpose : Handles user-related operations and authentication support.

        Responsibilities :
            - Fetch user by ID
            - Load user by email
            - Support Spring Security authentication

        Methods :

            getUserById()
                - Finds user using user ID
                - Throws exception if user is not found

            loadUserByUsername()
                - Finds user using email
                - Used by Spring Security during login
                - Returns authenticated user details

        Authentication Flow :

            Login Request
                    ↓
            Email Entered
                    ↓
            loadUserByUsername()
                    ↓
            User Loaded
                    ↓
            Password Verified
                    ↓
            Authentication Success

        Business Use :
            - User authentication
            - JWT authentication flow
            - User retrieval operations
            - Security context population
            - User loading during login

        Note :
            - Email acts as username.
            - Implements UserDetailsService.
            - Throws exceptions when user is not found.

        This class acts as the user management and authentication service.
*/