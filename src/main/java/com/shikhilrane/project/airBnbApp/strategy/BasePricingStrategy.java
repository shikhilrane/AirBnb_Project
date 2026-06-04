package com.shikhilrane.project.airBnbApp.strategy;

import com.shikhilrane.project.airBnbApp.entity.Inventory;

import java.math.BigDecimal;

public class BasePricingStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();          // Returns room base price
    }
}

/*
    BasePricingStrategy

        Purpose : Returns the base price of a room.

        Responsibilities :
            - Read room base price
            - Provide starting price for calculations
            - Act as the first pricing strategy

        Method :

            calculatePrice()
                - Gets room base price
                - Returns base price

        Business Use :
            - Used as the starting point of pricing.
            - Other pricing strategies build on top of this price.
            - Ensures every room has a default price.

        Example :

            Room Base Price = ₹1000

            Result = ₹1000

        Flow :

            Inventory
                ↓
            Room
                ↓
            Base Price
                ↓
            Return Price

        Note :
            - Does not apply any extra charges.
            - Returns only the original room price.
            - Used by all other pricing strategies.

        This strategy provides the default room price.
*/