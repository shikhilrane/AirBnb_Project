package com.shikhilrane.project.airBnbApp.repository;

import com.shikhilrane.project.airBnbApp.entity.Guest;
import com.shikhilrane.project.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);      // Retrieves all guests associated with a user
}

/*
    GuestRepository

        Purpose :
            Provides database access operations
            for Guest entities.

        Responsibilities :
            - Persist guest records
            - Retrieve guest records
            - Delete guest records
            - Query guests by user
            - Support guest management workflows

        Methods :

            findByUser()
                - Retrieves all guests belonging to a user

        Guest Management Flow :

            User
                ↓
            Create Guest
                ↓
            Guest Saved
                ↓
            Guest Repository
                ↓
            Database

        Guest Retrieval Flow :

            Authenticated User
                    ↓
            Guest List Request
                    ↓
            findByUser()
                    ↓
            Matching Guests
                    ↓
            API Response

        Entity Relationship :

            User
                ↓
            One-To-Many
                ↓
            Guests

            One User
                ↓
            Can Manage Multiple Guests

        Business Use :
            - Guest profile management
            - Booking guest selection
            - Traveler information storage
            - Customer account management

        Note :
            - Extends JpaRepository for standard CRUD operations.
            - Uses Spring Data JPA derived query methods.
            - Guest records are user-specific.
            - Supports retrieval of guests owned by a user.

        This repository acts as the primary
        data access layer for guest persistence
        and guest-related queries.
*/