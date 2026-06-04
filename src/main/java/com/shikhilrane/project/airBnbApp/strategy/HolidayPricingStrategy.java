package com.shikhilrane.project.airBnbApp.strategy;

import com.shikhilrane.project.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{
    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);                   // Gets price from previous strategy
        boolean isTodayHoliday = true;                                          // Placeholder holiday check
        if (isTodayHoliday) {
            price = price.multiply(BigDecimal.valueOf(1.25));                   // Adds 25% premium
        }
        return price;
    }
}

/*
    HolidayPricingStrategy

        Purpose : Increases room price during holidays.

        Responsibilities :
            - Detect holiday dates
            - Apply holiday premium
            - Adjust room pricing

        Method :

            calculatePrice()
                - Gets price from previous strategy
                - Checks holiday status
                - Applies holiday premium

        Business Use :
            - Increases revenue during holidays.
            - Reflects increased customer demand.
            - Supports festive season pricing.

        Example :

            Price = ₹1000

            Holiday Premium = 25%

            Result = ₹1250

        Flow :

            Inventory
                ↓
            Holiday Check
                ↓
            Apply Premium
                ↓
            Return Price

        Note :
            - Currently uses dummy holiday logic.
            - Can be connected to external holiday APIs.
            - Works with other pricing strategies.

        This strategy applies holiday-based room pricing.
*/