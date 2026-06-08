package com.shikhilrane.project.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelReportDto {
    private Long bookingCount;          // Total number of bookings in the report period
    private BigDecimal totalRevenue;    // Total revenue generated in the report period
    private BigDecimal avgRevenue;      // Average revenue per booking
}

/*
    HotelReportDto

        Purpose :
            Carries hotel performance and revenue statistics
            for reporting purposes.

        Responsibilities :
            - Represent booking metrics
            - Represent revenue metrics
            - Transfer report data to clients
            - Support business analytics

        Fields :

            bookingCount
                - Total bookings within the selected period

            totalRevenue
                - Total revenue generated from bookings

            avgRevenue
                - Average revenue earned per booking

        Report Generation Flow :

            Booking Records
                    ↓
            Revenue Calculation
                    ↓
            Report Aggregation
                    ↓
            HotelReportDto
                    ↓
            API Response

        Business Metrics :

            Booking Count
                - Measures booking volume

            Total Revenue
                - Measures overall earnings

            Average Revenue
                - Measures earning efficiency per booking

        Business Use :
            - Revenue tracking
            - Hotel performance monitoring
            - Business analytics
            - Financial reporting
            - Operational insights

        Note :
            - Used as a response DTO.
            - Contains aggregated reporting data.
            - Values are calculated in the service layer.
            - Supports date-range based reporting.

        This DTO acts as the reporting model
        for hotel performance and revenue analytics.
*/