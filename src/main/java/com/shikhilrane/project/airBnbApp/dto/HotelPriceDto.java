package com.shikhilrane.project.airBnbApp.dto;

import com.shikhilrane.project.airBnbApp.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDto {

    private Hotel hotel;      // Hotel details
    private Double price;     // Average/minimum hotel price for the requested date range
}

/*
    HotelPriceDto

        Purpose : Stores hotel details along with pricing information.

        This DTO stores :
            - Hotel details
            - Calculated hotel price

        Business Use :
            - Used in hotel search results.
            - Returns hotel information with price.
            - Helps users compare hotels by price.

        Example :

            Hotel : Hotel Lotus
            Price : ₹575

        Flow :

            HotelMinPrice
                ↓
            Aggregate Price
                ↓
            HotelPriceDto
                ↓
            API Response

        Note :
            - Used as a projection DTO.
            - Does not represent a database table.
            - Used only for transferring data.

        This DTO represents hotel pricing information
        returned to the client.
*/