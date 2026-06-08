package com.shikhilrane.project.airBnbApp.util;

import com.shikhilrane.project.airBnbApp.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {
    // Returns the currently authenticated user from Spring Security context
    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();            // Extracts authenticated user principal
    }
}

/*
    AppUtils

        Purpose :
            Provides common utility methods
            used across the application.

        Responsibilities :
            - Retrieve authenticated user
            - Simplify Security Context access
            - Avoid duplicate authentication code

        Methods :

            getCurrentUser()
                - Retrieves currently authenticated user
                - Reads user from Spring Security Context
                - Returns User entity

        Authentication Flow :

            Incoming Request
                    ↓
              JWT Filter
                    ↓
           User Authenticated
                    ↓
        Security Context Updated
                    ↓
            getCurrentUser()
                    ↓
              User Returned

        Usage Flow :

            Service Layer
                    ↓
            getCurrentUser()
                    ↓
          Security Context
                    ↓
            User Entity
                    ↓
            Business Logic

        Example :

            User user = AppUtils.getCurrentUser();

            user.getId();
            user.getEmail();
            user.getRoles();

        Business Use :
            - Profile management
            - Booking ownership validation
            - Guest ownership validation
            - Hotel ownership validation
            - Room ownership validation
            - Inventory ownership validation
            - Authentication-based operations

        Security Features :
            - Centralized user retrieval
            - Uses authenticated Security Context
            - Prevents duplicate authentication logic
            - Supports role-based authorization

        Note :
            - Assumes user is authenticated.
            - Returns User entity stored in Security Context.
            - Used across service layer classes.
            - Reduces repeated authentication code.

        This utility class acts as the central
        helper for retrieving the currently
        authenticated user.
*/