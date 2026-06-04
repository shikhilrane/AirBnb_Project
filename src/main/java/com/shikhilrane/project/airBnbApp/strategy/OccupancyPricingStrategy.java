package com.shikhilrane.project.airBnbApp.strategy;

import com.shikhilrane.project.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);                                   // Gets price from previous strategy
        double occupancyRate = (double) inventory.getBookedCount() / inventory.getTotalCount(); // Calculates occupancy percentage
        if(occupancyRate > 0.8) {
            price = price.multiply(BigDecimal.valueOf(1.2));                                    // Adds 20% premium
        }
        return price;
    }
}

/*
    OccupancyPricingStrategy

        Purpose : Increases room price based on occupancy level.

        Responsibilities :
            - Calculate occupancy rate
            - Apply occupancy premium
            - Adjust room price

        Method :

            calculatePrice()
                - Gets price from previous strategy
                - Calculates occupancy rate
                - Applies occupancy premium if required

        Business Use :
            - Increases revenue during high occupancy.
            - Reflects room demand.
            - Supports smart pricing.

        Example :

            Total Rooms  = 100
            Booked Rooms = 85

            Occupancy = 85%

            Result = Price + 20%

        Flow :

            Inventory
                ↓
            Calculate Occupancy
                ↓
            Occupancy > 80%
                ↓
            Increase Price
                ↓
            Return Price

        Note :
            - Premium is applied only when occupancy exceeds 80%.
            - Uses booked rooms and total rooms.
            - Helps optimize hotel earnings.

        This strategy adjusts pricing based on occupancy percentage.
*/