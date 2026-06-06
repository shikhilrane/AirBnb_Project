package com.shikhilrane.project.airBnbApp.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    public StripeConfig(@Value("${stripe.secret.key}") String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }
}

/*
    StripeConfig

        Purpose : Configures Stripe payment gateway for the application.
                  Initializes the Stripe SDK using the secret API key.

        Responsibilities :
            - Load Stripe secret key from application properties
            - Initialize Stripe SDK
            - Enable Stripe API operations across the application

        Configuration Flow :

            application.properties
                    ↓
            stripe.secret.key
                    ↓
            StripeConfig
                    ↓
            Stripe.apiKey Initialized
                    ↓
            Stripe Services Ready

        Stripe Features Enabled :
            - Checkout Session Creation
            - Payment Processing
            - Payment Confirmation
            - Refund Processing
            - Webhook Event Handling
            - Customer Creation

        Payment Flow :

            Booking Created
                    ↓
            Checkout Session Generated
                    ↓
            User Completes Payment
                    ↓
            Stripe Webhook Triggered
                    ↓
            Booking Confirmed

        Example Property :

            stripe.secret.key=
            sk_test_xxxxxxxxxxxxxxxxxxxxxxxxx

        Security Features :
            - Secret key stored externally
            - No hardcoded credentials
            - Supports environment-based configuration
            - Stripe handles payment security

        Note :
            - Stripe secret key must be valid.
            - Configuration is loaded during application startup.
            - Stripe SDK is globally initialized once.
            - Required before any Stripe API call.

        This configuration class acts as the Stripe
        payment gateway initialization layer
        of the application.
*/