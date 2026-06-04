package com.shikhilrane.project.airBnbApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HotelMinPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                    // Unique identifier for hotel minimum price record

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;                                // Hotel associated with this pricing record

    @Column(nullable = false)
    private LocalDate date;                             // Date for which minimum price is stored

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;                           // Cheapest room price available on this date

    @CreationTimestamp
    private LocalDateTime createdAt;                    // Timestamp when record was created

    @UpdateTimestamp
    private LocalDateTime updatedAt;                    // Timestamp when record was last updated

    public HotelMinPrice(Hotel hotel, LocalDate date) {
        this.hotel = hotel;                             // Sets hotel reference
        this.date = date;                               // Sets pricing date
    }
}

/*
    HotelMinPrice Entity

        Purpose : Stores the minimum room price of a hotel for each day.

        This entity stores :
            - Hotel information
            - Date
            - Minimum room price
            - Audit information (createdAt, updatedAt)

        Relationships :
            - Many HotelMinPrice Records -> One Hotel

        Example :

            HotelMinPrice #101

                Hotel   : Hotel Lotus
                Date    : 10 Jun 2026
                Price   : ₹575

        Business Use :
            - Supports fast hotel search.
            - Avoids scanning the Inventory table during searches.
            - Stores pre-calculated hotel prices.
            - Improves search performance.

        Example :

            Hotel Lotus

                10 Jun -> ₹575
                11 Jun -> ₹500
                12 Jun -> ₹500

        Flow :

            Inventory Prices
                    ↓
            Find Minimum Price
                    ↓
            HotelMinPrice
                    ↓
            Hotel Search

        Note :
            - One record exists for a Hotel + Date combination.
            - Price represents the cheapest room available for that day.
            - Updated automatically by the pricing scheduler.

        This table acts as a cache for hotel pricing
        and helps speed up hotel search operations.
*/