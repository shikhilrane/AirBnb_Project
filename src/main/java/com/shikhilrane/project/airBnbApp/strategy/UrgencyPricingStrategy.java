package com.shikhilrane.project.airBnbApp.strategy;

import com.shikhilrane.project.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);       // Gets price from previous strategy
        LocalDate today = LocalDate.now();
        if(!inventory.getDate().isBefore(today) && inventory.getDate().isBefore(today.plusDays(7))) {
            price = price.multiply(BigDecimal.valueOf(1.15));       // Adds 15% premium
        }
        return price;
    }
}

/*
    UrgencyPricingStrategy

        Purpose : Increases room price for near-future bookings.

        Responsibilities :
            - Check booking date
            - Apply urgency premium
            - Adjust room pricing

        Method :

            calculatePrice()
                - Gets price from previous strategy
                - Checks room date
                - Applies urgency premium if required

        Business Use :
            - Increases revenue from last-minute bookings.
            - Creates urgency-based pricing.
            - Supports dynamic pricing.

        Example :

            Today        = 01 Jan
            Booking Date = 05 Jan

            Result = Price + 15%

        Flow :

            Inventory Date
                ↓
            Within Next 7 Days
                ↓
            Increase Price
                ↓
            Return Price

        Note :
            - Premium is applied only for bookings within 7 days.
            - Uses current date for calculation.
            - Encourages early booking.

        This strategy adjusts pricing based on booking urgency.
*/