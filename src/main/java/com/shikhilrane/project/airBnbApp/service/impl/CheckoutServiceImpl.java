package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.entity.Booking;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.repository.BookingRepository;
import com.shikhilrane.project.airBnbApp.service.CheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final BookingRepository bookingRepository; // Persists Stripe session information in booking records

    // Creates Stripe checkout session and returns payment URL
    @Override
    public String getCheckoutSession(
            Booking booking,
            String successUrl,
            String failureUrl) {

        log.info(
                "Creating session for booking with ID: {}",
                booking.getId()
        ); // Logs checkout session creation request

        User user =
                (User) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal(); // Fetches currently authenticated user

        try {

            CustomerCreateParams customerParams =
                    CustomerCreateParams.builder()
                            .setName(user.getName())     // Sets Stripe customer name
                            .setEmail(user.getEmail())   // Sets Stripe customer email
                            .build();

            Customer customer =
                    Customer.create(customerParams); // Creates customer in Stripe

            SessionCreateParams sessionParams =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT) // Creates one-time payment session
                            .setBillingAddressCollection(
                                    SessionCreateParams.BillingAddressCollection.REQUIRED
                            ) // Requires billing address during checkout
                            .setCustomer(customer.getId()) // Associates session with Stripe customer
                            .setSuccessUrl(successUrl) // Redirect URL after successful payment
                            .setCancelUrl(failureUrl) // Redirect URL after failed/cancelled payment
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L) // Creates single booking payment item
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("inr") // Payment currency
                                                            .setUnitAmount(
                                                                    booking.getAmount()
                                                                            .multiply(BigDecimal.valueOf(100))
                                                                            .longValue()
                                                            ) // Converts rupees into paise for Stripe
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName(
                                                                                    booking.getHotel().getName()
                                                                                            + " : "
                                                                                            + booking.getRoom().getType()
                                                                            ) // Displays hotel and room information
                                                                            .setDescription(
                                                                                    "Booking ID: "
                                                                                            + booking.getId()
                                                                            ) // Displays booking reference
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            Session session =
                    Session.create(sessionParams); // Creates Stripe checkout session

            booking.setPaymentSessionId(
                    session.getId()
            ); // Stores Stripe session ID in booking

            bookingRepository.save(
                    booking
            ); // Persists session ID for webhook lookup

            log.info(
                    "Session created successfully for booking with ID: {}",
                    booking.getId()
            ); // Logs successful session creation

            return session.getUrl(); // Returns Stripe hosted checkout URL

        } catch (StripeException e) {

            e.printStackTrace(); // Logs Stripe exception

            throw new RuntimeException(); // Propagates checkout creation failure
        }
    }
}


/*
    CheckoutServiceImpl

        Purpose :
            Handles Stripe checkout session creation.
            Acts as the payment gateway integration layer.

        Responsibilities :
            - Create Stripe customers
            - Create Stripe checkout sessions
            - Configure payment information
            - Store Stripe session IDs
            - Return checkout URLs
            - Support webhook payment confirmation

        Methods :

            getCheckoutSession()
                - Creates Stripe customer
                - Creates Stripe checkout session
                - Stores session ID
                - Returns payment URL

        Payment Flow :

            Booking Created
                    ↓
            initiatePayments()
                    ↓
          CheckoutServiceImpl
                    ↓
           Create Customer
                    ↓
         Create Checkout Session
                    ↓
         Store Session ID
                    ↓
          Return Session URL
                    ↓
          User Payment Page

        Stripe Checkout Flow :

            User
                    ↓
            Stripe Customer
                    ↓
          Checkout Session
                    ↓
           Hosted Payment Page
                    ↓
           Payment Success
                    ↓
             Webhook Event
                    ↓
           Booking Confirmed

        Customer Creation Flow :

            Authenticated User
                    ↓
                Name
                Email
                    ↓
           CustomerCreateParams
                    ↓
            Stripe Customer

        Session Creation Flow :

            Booking
                    ↓
             Amount
                    ↓
             Line Item
                    ↓
          SessionCreateParams
                    ↓
            Stripe Session

        Amount Conversion :

            Booking Amount

                ₹5000

                    ↓

            Stripe Amount

                500000

            (Amount × 100)

        Session Information Stored :

            Session ID
                    ↓
        booking.paymentSessionId
                    ↓
          Webhook Lookup

        Business Use :
            - Online payments
            - Booking confirmation
            - Stripe integration
            - Customer creation
            - Checkout management
            - Payment tracking

        Security Features :
            - Stripe hosted checkout page
            - No card data stored locally
            - Session-based payment tracking
            - Webhook-based confirmation

        Example :

            Booking ID      = 15
            Amount          = ₹5000

                    ↓

            Stripe Session

            Session ID      = cs_test_xxxxx

                    ↓

            Session URL Returned

        Note :
            - Uses Stripe Checkout.
            - Creates customer for every payment.
            - Stores session ID for webhook processing.
            - Payment confirmation happens through webhooks.
            - Stripe amount is always sent in smallest currency unit.

        This service acts as the Stripe
        checkout integration layer
        of the application.
*/