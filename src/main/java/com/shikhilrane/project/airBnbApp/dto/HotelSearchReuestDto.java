package com.shikhilrane.project.airBnbApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchReuestDto {
    @NotBlank(message = "City is required")
    private String city;                           // City where user wants to search hotels

    @NotNull(message = "Start date is required")
    private LocalDate startDate;                   // Check-in date

    @NotNull(message = "End date is required")
    private LocalDate endDate;                     // Check-out date

    @NotNull(message = "Rooms count is required")
    @Positive(message = "Rooms count must be greater than 0")
    private Integer roomsCount;                    // Number of rooms required

    @PositiveOrZero(message = "Page cannot be negative")
    private Integer page = 0;                      // Page number for pagination

    @Positive(message = "Size must be greater than 0")
    private Integer size = 10;                     // Number of records per page
}

/*
    HotelSearchRequestDto

        Purpose : Receives hotel search criteria from guests.

        This DTO contains :
            - City
            - Check-in date
            - Check-out date
            - Required rooms count
            - Pagination details

        Validations :
            - City cannot be blank
            - Start date cannot be null
            - End date cannot be null
            - Rooms count must be greater than 0
            - Page cannot be negative
            - Size must be greater than 0

        Example Request :

            {
                "city": "Mumbai",
                "startDate": "2026-06-10",
                "endDate": "2026-06-12",
                "roomsCount": 2,
                "page": 0,
                "size": 10
            }

        Business Use :
            - Used to search hotels.
            - Filters hotels by city and date range.
            - Checks room availability.
            - Supports pagination.

        Search Flow :

                City
                  +
             Date Range
                  +
            Rooms Count
                  ↓
            Search Request
                  ↓
            Available Hotels

        Note :
            - This is not a database entity.
            - Used only for incoming API requests.

        This DTO represents hotel search criteria.
*/