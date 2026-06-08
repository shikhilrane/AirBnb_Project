package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.ProfileUpdateRequestDto;
import com.shikhilrane.project.airBnbApp.dto.UserDto;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.repository.UserRepository;
import com.shikhilrane.project.airBnbApp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.shikhilrane.project.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;    // Performs user database operations
    private final ModelMapper modelMapper;          // Converts entities into DTOs and vice versa

    // Retrieves user details using user identifier
    @Override
    public User getUserById(Long id) {
        return userRepository
                .findById(id)                                                                               // Searches user by identifier
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));     // Throws exception if user does not exist
    }

    // Loads user details using email for Spring Security authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username)                                                                                   // Searches user using email
                .orElseThrow(() -> new BadCredentialsException("User with entered email " + username + " not found"));  // Throws exception if email does not exist
    }

    // Updates profile information of the currently authenticated user
    @Override
    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {

        User user = getCurrentUser();                                    // Retrieves currently authenticated user

        if (profileUpdateRequestDto.getDateOfBirth() != null)
            user.setDateOfBirth(profileUpdateRequestDto.getDateOfBirth()); // Updates date of birth if provided

        if (profileUpdateRequestDto.getGender() != null)
            user.setGender(profileUpdateRequestDto.getGender());           // Updates gender if provided

        if (profileUpdateRequestDto.getName() != null)
            user.setName(profileUpdateRequestDto.getName());               // Updates name if provided

        userRepository.save(user);                                         // Persists updated profile information
    }

    // Retrieves profile information of the currently authenticated user
    @Override
    public UserDto getMyProfile() {

        User user = getCurrentUser();                                      // Retrieves currently authenticated user

        log.info("Getting the profile for user with id: {}", user.getId()); // Logs profile retrieval request

        return modelMapper.map(user, UserDto.class);                       // Converts entity into DTO
    }
}

/*
    UserServiceImpl

        Purpose :
            Handles user management and authentication-related operations.
            Integrates with Spring Security for user authentication.

        Responsibilities :
            - Retrieve users by identifier
            - Load users for authentication
            - Update user profile
            - Retrieve current user profile
            - Support Spring Security authentication flow

        Methods :

            getUserById()
                - Finds user using user ID
                - Throws exception if user is not found

            loadUserByUsername()
                - Finds user using email
                - Used by Spring Security authentication
                - Returns authenticated user details

            updateProfile()
                - Updates profile information
                - Supports partial profile updates
                - Updates authenticated user's profile

            getMyProfile()
                - Retrieves authenticated user's profile
                - Returns profile information as DTO

        Authentication Flow :

            Login Request
                    ↓
            Email Entered
                    ↓
            loadUserByUsername()
                    ↓
            User Loaded
                    ↓
            Password Verification
                    ↓
            Authentication Success

        Profile Update Flow :

            Authenticated User
                    ↓
            Profile Update Request
                    ↓
            Validate Input
                    ↓
            Update User Fields
                    ↓
            Save User
                    ↓
            Profile Updated

        Profile Retrieval Flow :

            Authenticated User
                    ↓
            Profile Request
                    ↓
            getMyProfile()
                    ↓
            User Entity
                    ↓
            UserDto
                    ↓
            API Response

        Business Use :
            - User authentication
            - User profile management
            - JWT authentication flow
            - Security context population
            - User information retrieval
            - Profile maintenance

        Security Features :
            - Spring Security integration
            - Authenticated profile access
            - User identity validation
            - Secure user retrieval

        Note :
            - Email acts as username.
            - Implements UserDetailsService through UserService.
            - Profile updates are partial and optional.
            - Authenticated user is retrieved from Security Context.
            - Exceptions are thrown when users are not found.

        This service acts as the central
        user management and authentication
        component of the application.
*/