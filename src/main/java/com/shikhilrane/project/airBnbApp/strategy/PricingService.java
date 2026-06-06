package com.shikhilrane.project.airBnbApp.strategy;

import com.shikhilrane.project.airBnbApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {
    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        PricingStrategy pricingStrategy = new BasePricingStrategy();       // Starts with base price calculation

        pricingStrategy = new SurgePricingStrategy(pricingStrategy);       // Applies surge pricing
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);   // Applies occupancy pricing
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);     // Applies urgency pricing
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);     // Applies holiday pricing

        return pricingStrategy.calculatePrice(inventory);                  // Returns final dynamic price
    }

    // Return the sum of price of this inventory list
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {
        return inventoryList.stream()
                .map(inventory -> calculateDynamicPricing(inventory))       // Calculates dynamic price for each inventory
                .reduce(BigDecimal.ZERO, BigDecimal::add);                           // Sums all calculated prices
    }
}

/*
    PricingService

        Purpose : Calculates room prices using dynamic pricing strategies.
                  Acts as the central pricing engine of the application.

        Responsibilities :
            - Calculate room prices dynamically
            - Build pricing strategy chain
            - Apply pricing adjustments
            - Calculate total booking amount
            - Combine multiple pricing rules

        Methods :

            calculateDynamicPricing()
                - Creates pricing strategy chain
                - Applies all pricing decorators
                - Returns final room price

            calculateTotalPrice()
                - Calculates dynamic price for every inventory
                - Sums all inventory prices
                - Returns final booking amount

        Business Use :
            - Dynamic hotel room pricing
            - Booking amount calculation
            - Revenue optimization
            - Seasonal price adjustments
            - Occupancy-based pricing
            - Demand-based pricing

        Dynamic Pricing Flow :

            Inventory
                ↓
            Base Pricing
                ↓
            Surge Pricing
                ↓
            Occupancy Pricing
                ↓
            Urgency Pricing
                ↓
            Holiday Pricing
                ↓
            Final Dynamic Price

        Booking Amount Flow :

            Inventory List
                    ↓
            calculateDynamicPricing()
                    ↓
            Individual Prices
                    ↓
                Sum All
                    ↓
            Total Booking Amount

        Example :

            Day 1 Price = ₹1200
            Day 2 Price = ₹1500
            Day 3 Price = ₹1800

            Total Booking Amount

            ₹1200 + ₹1500 + ₹1800 = ₹4500

        Pricing Factors :

            Surge Pricing
                - High demand period

            Occupancy Pricing
                - Hotel occupancy percentage

            Urgency Pricing
                - Last-minute bookings

            Holiday Pricing
                - Festivals and holidays

        Design Patterns :

            Strategy Pattern
                - Encapsulates pricing algorithms

            Decorator Pattern
                - Chains pricing strategies together

        Benefits :
            - Flexible pricing engine
            - Easy strategy extension
            - Open/Closed Principle compliant
            - Reusable pricing logic
            - Centralized price calculation

        Note :
            - Base price comes from inventory room configuration.
            - Strategies execute sequentially.
            - New pricing rules can be added easily.
            - Total booking amount is calculated using dynamic prices.

        This service acts as the central
        dynamic pricing engine of the application.
*/