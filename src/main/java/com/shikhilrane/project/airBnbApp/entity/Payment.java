package com.shikhilrane.project.airBnbApp.entity;

import com.shikhilrane.project.airBnbApp.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                    // Unique identifier for each payment

    @Column(unique = true, nullable = false)
    private String transactionId;                       // Unique transaction reference from payment gateway

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;                // Current payment status (PENDING, CONFIRMED, CANCELLED)

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;                          // Amount paid by the user

    @CreationTimestamp
    private LocalDateTime createdAt;                    // Timestamp when payment record was created

    @UpdateTimestamp
    private LocalDateTime updatedAt;                    // Timestamp when payment record was last updated

    @OneToOne(fetch = FetchType.LAZY)
    private Booking booking;                            // Booking associated with this payment
}

/*
    Payment Entity

    Purpose : Represents a payment made for a hotel booking.
              Stores transaction details, payment amount,
              and the current payment status.

        This entity stores :
        - Unique transaction identifier
        - Payment status
        - Payment amount
        - Associated booking
        - Audit information (createdAt, updatedAt)

    Relationships :
        - One Payment -> One Booking
        - One Booking -> One Payment

    Example :
        Payment #501
            Transaction ID : TXN_ABC123XYZ
            Booking ID     : 101
            Amount         : ₹12,000
            Status         : SUCCESS

    Business Use :
        - Tracks payment transactions for bookings.
        - Links payments with bookings.
        - Maintains payment status throughout the payment lifecycle.
        - Helps prevent duplicate payments using transaction IDs.
        - Provides payment history for audits and reporting.
        - Acts as proof of payment for booking confirmation.

    Payment Lifecycle :

        PENDING
            ↓
        SUCCESS

        OR

        PENDING
            ↓
        FAILED

        OR

        SUCCESS
            ↓
        REFUNDED

    Example Scenarios :

        Successful Payment

            Booking ID     : 101
            Amount         : ₹8,500
            Transaction ID : TXN_12345
            Status         : SUCCESS

        Failed Payment

            Booking ID     : 102
            Amount         : ₹8,500
            Transaction ID : TXN_67890
            Status         : FAILED

    Note :
        - Transaction ID must be unique.
        - One payment belongs to exactly one booking.
        - Amount is stored using BigDecimal for accurate monetary calculations.
        - A booking is typically confirmed only after successful payment.

    Each record in the "payment" table represents one payment transaction.
*/