package com.shikhilrane.project.airBnbApp.repository;

import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {
    List<Hotel> findByOwner(User user);      // Retrieves all hotels owned by a specific user
}

/*
    HotelRepository

        Purpose :
            Provides database access operations
            for Hotel entities.

        Responsibilities :
            - Persist hotel records
            - Retrieve hotel records
            - Delete hotel records
            - Query hotels by owner
            - Support hotel management operations

        Methods :

            findByOwner()
                - Retrieves all hotels owned by a user

        Hotel Management Flow :

            Hotel Manager
                    ↓
            Create Hotel
                    ↓
            Hotel Saved
                    ↓
            HotelRepository
                    ↓
            Database

        Hotel Retrieval Flow :

            Hotel Manager
                    ↓
            View My Hotels
                    ↓
            findByOwner()
                    ↓
            Matching Hotels
                    ↓
            API Response

        Entity Relationship :

            User
                ↓
            One-To-Many
                ↓
            Hotels

            One Hotel Manager
                    ↓
            Can Own Multiple Hotels

        Business Use :
            - Hotel management
            - Hotel ownership validation
            - Hotel listing
            - Hotel administration
            - Reporting support

        Note :
            - Extends JpaRepository for standard CRUD operations.
            - Uses Spring Data JPA derived query methods.
            - Hotels are associated with owners.
            - Supports ownership-based hotel retrieval.

        This repository acts as the primary
        data access layer for hotel persistence
        and hotel ownership queries.
*/