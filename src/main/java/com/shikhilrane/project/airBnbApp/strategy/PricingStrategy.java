package com.shikhilrane.project.airBnbApp.strategy;

import com.shikhilrane.project.airBnbApp.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);         // Calculates room price based on inventory data
}

/*
    PricingStrategy

        Purpose : Defines the contract for room pricing calculation.

        Responsibilities :
            - Calculate room price
            - Support multiple pricing algorithms
            - Enable dynamic pricing strategies

        Method :

            calculatePrice()
                - Accepts inventory details
                - Returns calculated room price

        Business Use :
            - Calculates room prices dynamically.
            - Supports surge pricing.
            - Allows different pricing models.
            - Follows Strategy Design Pattern.

        Example Strategies :

            BasePricingStrategy
                - Returns base room price

            SurgePricingStrategy
                - Applies surge factor

            WeekendPricingStrategy
                - Applies weekend pricing rules

            FestivalPricingStrategy
                - Applies festival pricing rules

        Flow :

            Inventory
                ↓
        PricingStrategy
                ↓
        calculatePrice()
                ↓
            Price

        Strategy Pattern Benefit :

            Without Strategy :
                if-else
                switch-case
                complex pricing logic

            With Strategy :
                PricingStrategy
                    ↓
            Different Implementations

        Note :
            - Interface contains only pricing contract.
            - Actual pricing logic is implemented by strategy classes.
            - Makes pricing system extensible.

        This interface acts as the base contract
        for all pricing strategies.
*/