package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final BookingService bookingService;        // Handles booking confirmation and payment processing

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;                      // Stripe webhook signing secret used for signature verification

    // Receives Stripe webhook events and forwards them for booking processing
    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);   // Verifies webhook signature and constructs Stripe event
            bookingService.capturePayment(event);                                       // Processes Stripe event and updates booking status
            return ResponseEntity.noContent().build();                                  // Returns HTTP 204 after successful processing
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature", e);                           // Prevents processing of forged webhook requests
            throw new RuntimeException(e);
        }
    }
}

/*
    WebhookController

        Purpose :
            Handles Stripe webhook requests.
            Acts as the entry point for payment confirmation events.

        Responsibilities :
            - Receive Stripe webhook requests
            - Verify Stripe signatures
            - Construct Stripe events
            - Forward events to BookingService
            - Prevent unauthorized webhook processing

        Endpoints :

            POST /webhook/payment
                - Receives Stripe webhook events
                - Verifies webhook signature
                - Processes payment events

        Webhook Flow :

            Stripe Checkout
                    ↓
             Payment Success
                    ↓
            Stripe Webhook
                    ↓
          WebhookController
                    ↓
         Signature Validation
                    ↓
            BookingService
                    ↓
            Booking Updated

        Payment Confirmation Flow :

            User Completes Payment
                    ↓
       checkout.session.completed
                    ↓
            Stripe Webhook
                    ↓
          capturePayments()
                    ↓
         bookingService.capturePayment()
                    ↓
            Booking Confirmed

        Session Expiration Flow :

            Checkout Session
                    ↓
                Expires
                    ↓
      checkout.session.expired
                    ↓
            Stripe Webhook
                    ↓
          capturePayments()
                    ↓
         bookingService.capturePayment()
                    ↓
            Booking Cancelled

        Security Flow :

            Stripe Request
                    ↓
          Stripe-Signature
                    ↓
         Webhook.constructEvent()
                    ↓
         Signature Verified
                    ↓
            Event Accepted

            OR

         Signature Invalid
                    ↓
      SignatureVerificationException
                    ↓
          Request Rejected

        Business Use :
            - Payment confirmation
            - Booking confirmation
            - Booking cancellation
            - Inventory synchronization
            - Stripe integration

        Security Features :
            - Stripe signature verification
            - Webhook authenticity validation
            - Forged request prevention
            - Secure payment processing

        Error Handling :

            Invalid Signature
                    ↓
      SignatureVerificationException
                    ↓
             Error Logged
                    ↓
          Request Rejected

        Note :
            - Stripe sends webhook events asynchronously.
            - Every request must pass signature validation.
            - Booking logic is delegated to BookingService.
            - Successful processing returns HTTP 204.
            - Supports Stripe payment lifecycle events.

        This controller acts as the Stripe
        webhook entry point of the application.
*/