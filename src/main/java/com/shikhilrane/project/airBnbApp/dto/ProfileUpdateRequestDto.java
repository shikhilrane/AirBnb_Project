package com.shikhilrane.project.airBnbApp.dto;

import com.shikhilrane.project.airBnbApp.entity.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDto {
    private String name;                // Updated name of the user
    private LocalDate dateOfBirth;      // Updated date of birth
    private Gender gender;              // Updated gender information
}

/*
    ProfileUpdateRequestDto

        Purpose :
            Carries profile update information
            submitted by an authenticated user.

        Responsibilities :
            - Transfer profile update data
            - Support user profile modifications
            - Carry personal information updates

        Fields :

            name
                - Updated user name

            dateOfBirth
                - Updated date of birth

            gender
                - Updated gender value

        Profile Update Flow :

            User
                ↓
            Edit Profile
                ↓
            Submit Changes
                ↓
            ProfileUpdateRequestDto
                ↓
            Service Layer
                ↓
            Profile Updated

        Business Use :
            - Profile management
            - Personal information updates
            - User account maintenance
            - Customer self-service operations

        Note :
            - Used only for profile update requests.
            - Contains user-editable profile fields.
            - Validation and business rules are handled in the service layer.
            - Supports partial profile modification workflows.

        This DTO acts as the request model
        for updating authenticated user profile information.
*/