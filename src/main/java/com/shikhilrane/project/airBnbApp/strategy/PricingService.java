package com.shikhilrane.project.airBnbApp.strategy;

import com.shikhilrane.project.airBnbApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
}

/*
    PricingService

        Purpose : Calculates final room price using multiple pricing strategies.

        Responsibilities :
            - Build pricing chain
            - Apply pricing rules
            - Return final room price

        Method :

            calculateDynamicPricing()
                - Creates pricing strategy chain
                - Applies all pricing rules
                - Returns final calculated price

        Business Use :
            - Central pricing engine.
            - Supports dynamic room pricing.
            - Combines multiple pricing factors.
            - Calculates booking price accurately.

        Pricing Flow :

            Base Price
                ↓
            Surge Pricing
                ↓
            Occupancy Pricing
                ↓
            Urgency Pricing
                ↓
            Holiday Pricing
                ↓
            Final Price

        Example :

            Base Price      = ₹1000
            Surge           = +50%
            Occupancy       = +20%
            Urgency         = +15%
            Holiday         = +25%

            Final Price     = ₹2587.50

        Design Patterns :

            Strategy Pattern
                - Multiple pricing implementations

            Decorator Pattern
                - Chains pricing rules together

        Note :
            - Easy to add new pricing strategies.
            - Existing pricing logic remains unchanged.
            - Supports Open/Closed Principle.

        This service acts as the central pricing engine of the application.
*/