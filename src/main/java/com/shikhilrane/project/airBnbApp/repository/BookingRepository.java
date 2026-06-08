package com.shikhilrane.project.airBnbApp.repository;

import com.shikhilrane.project.airBnbApp.entity.Booking;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);     // Finds booking using Stripe checkout session ID
    List<Booking> findByHotel(Hotel hotel);                        // Retrieves all bookings of a hotel
    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, LocalDateTime startDateTime, LocalDateTime endDateTime); // Retrieves bookings within a date range
    List<Booking> findByUser(User user);                           // Retrieves all bookings of a user
}

/*
    BookingRepository

        Purpose :
            Provides database access operations
            for Booking entities.

        Responsibilities :
            - Persist bookings
            - Retrieve bookings
            - Query bookings by hotel
            - Query bookings by user
            - Query bookings by payment session
            - Support reporting operations

        Methods :

            findByPaymentSessionId()
                - Finds booking using Stripe checkout session ID

            findByHotel()
                - Retrieves all bookings belonging to a hotel

            findByHotelAndCreatedAtBetween()
                - Retrieves hotel bookings within a date range

            findByUser()
                - Retrieves bookings created by a user

        Booking Query Flow :

            Service Layer
                    ↓
            Repository Method
                    ↓
            Database Query
                    ↓
            Matching Bookings
                    ↓
            Return Result

        Payment Lookup Flow :

            Stripe Webhook
                    ↓
            Session ID Received
                    ↓
            findByPaymentSessionId()
                    ↓
            Booking Retrieved
                    ↓
            Booking Updated

        Hotel Reporting Flow :

            Hotel Report Request
                    ↓
            Date Range Selected
                    ↓
            findByHotelAndCreatedAtBetween()
                    ↓
            Matching Bookings
                    ↓
            Revenue Calculation

        User Booking Flow :

            Authenticated User
                    ↓
            My Bookings Request
                    ↓
            findByUser()
                    ↓
            Booking History Returned

        Business Use :
            - Booking management
            - Booking history retrieval
            - Hotel reporting
            - Revenue analytics
            - Stripe payment processing
            - Webhook handling

        Note :
            - Extends JpaRepository for standard CRUD operations.
            - Uses Spring Data JPA derived query methods.
            - Supports hotel-specific reporting queries.
            - Supports user booking history retrieval.
            - Session ID lookup is used during payment confirmation.

        This repository acts as the primary
        data access layer for booking persistence
        and booking-related queries.
*/