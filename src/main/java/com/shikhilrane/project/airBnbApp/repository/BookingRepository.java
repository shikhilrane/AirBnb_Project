package com.shikhilrane.project.airBnbApp.repository;

import com.shikhilrane.project.airBnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);     // Finds booking using Stripe checkout session ID
}

/*
    BookingRepository

        Purpose :
            Provides database operations
            for Booking records.

        Responsibilities :
            - Save bookings
            - Fetch bookings
            - Delete bookings
            - Find bookings by ID
            - Find bookings by Stripe session ID

        Methods :

            findByPaymentSessionId()
                - Finds booking associated with
                  a Stripe checkout session

        Booking Lifecycle :

            Booking Created
                    ↓
                RESERVED
                    ↓
            Payment Initiated
                    ↓
            Stripe Session Created
                    ↓
          paymentSessionId Saved
                    ↓
            Payment Completed
                    ↓
        findByPaymentSessionId()
                    ↓
                CONFIRMED

        Payment Flow :

            Booking
                    ↓
        Stripe Checkout Session
                    ↓
             Session ID
                    ↓
        Stored In Booking
                    ↓
           Webhook Event
                    ↓
        findByPaymentSessionId()
                    ↓
          Fetch Booking

        Business Use :
            - Stripe payment processing
            - Webhook handling
            - Booking confirmation
            - Booking lookup

        Example :

            Booking

                ID                = 15
                Session ID        = cs_test_abc123

            Query

                findByPaymentSessionId(
                    "cs_test_abc123"
                )

            Result

                Booking #15

        Query Logic :

            sessionId
                    ↓
            Search Booking
                    ↓
             Matching Record
                    ↓
            Return Booking

        Note :
            - Used during Stripe webhook processing.
            - Session ID is unique per checkout session.
            - Returns Optional to handle missing bookings.
            - Supports payment confirmation workflow.

        This repository acts as the data access layer
        for booking persistence and payment session lookup.
*/