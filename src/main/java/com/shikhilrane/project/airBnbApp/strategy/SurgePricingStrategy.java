package com.shikhilrane.project.airBnbApp.strategy;

import com.shikhilrane.project.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);   // Gets price from previous strategy
        return price.multiply(inventory.getSurgeFactor());      // Applies surge factor
    }
}

/*
    SurgePricingStrategy

        Purpose : Applies surge pricing on the room price.

        Responsibilities :
            - Read surge factor
            - Increase room price
            - Support demand-based pricing

        Method :

            calculatePrice()
                - Gets price from previous strategy
                - Applies surge factor
                - Returns updated price

        Business Use :
            - Increases room price during high demand.
            - Supports dynamic pricing.
            - Helps maximize hotel revenue.

        Example :

            Base Price   = ₹1000
            Surge Factor = 1.50

            Result = ₹1500

        Flow :

            Inventory
                ↓
            Previous Price
                ↓
            Apply Surge Factor
                ↓
            Return Price

        Note :
            - Uses inventory surge factor.
            - Works on top of previous pricing strategies.
            - Can increase or decrease room price.

        This strategy applies surge pricing to room rates.
*/