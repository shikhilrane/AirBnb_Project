package com.shikhilrane.project.airBnbApp.repository;

import com.shikhilrane.project.airBnbApp.dto.HotelPriceDto;
import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {

    // Returns hotels with average prices for the requested date range
    @Query("""
            SELECT new com.shikhilrane.project.airBnbApp.dto.HotelPriceDto(i.hotel, AVG(i.price))
            FROM HotelMinPrice i
            WHERE i.hotel.city = :city
                AND i.date BETWEEN :startDate AND :endDate
                AND i.hotel.active = true
           GROUP BY i.hotel
           """)
    Page<HotelPriceDto> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    // Finds minimum price record for a hotel on a specific date
    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}

/*
    HotelMinPriceRepository

        Purpose : Provides database operations for HotelMinPrice.

        Responsibilities :
            - Save hotel minimum prices
            - Fetch hotel pricing data
            - Support hotel search queries

        Methods :

            findHotelsWithAvailableInventory()
                - Finds hotels by city and date range
                - Calculates average hotel price
                - Returns paginated results

            findByHotelAndDate()
                - Finds minimum price record
                  for a hotel on a specific date

        Business Use :
            - Supports hotel search.
            - Provides pricing information.
            - Improves search performance by using
              pre-calculated hotel prices.

        Search Flow :

            Search Request
                    ↓
            HotelMinPrice Table
                    ↓
            Average Price Calculation
                    ↓
            HotelPriceDto
                    ↓
            API Response

        Example :

            City = Mumbai
            Dates = 10 Jun - 12 Jun

            Result :

                Hotel Lotus  -> ₹575
                Hotel Taj    -> ₹700

        Note :
            - Uses JPQL projection.
            - Returns HotelPriceDto directly.
            - Works on HotelMinPrice table instead of Inventory.

        This repository handles hotel pricing queries.
*/

/*
    findHotelsWithAvailableInventory Query :

        Purpose :
            Finds hotels along with their average price
            for the requested stay duration.

        Logic :
            1. Filter hotels by city.
            2. Filter records by date range.
            3. Consider only active hotels.
            4. Calculate average hotel price.
            5. Return paginated results.

        Example :

            Search :

                City       = Mumbai
                Check-In   = 10 Jun
                Check-Out  = 12 Jun

            HotelMinPrice Records :

                10 Jun -> ₹500
                11 Jun -> ₹600
                12 Jun -> ₹700

            Average Price :

                (500 + 600 + 700) / 3
                = ₹600

        Result :
            Returns hotels with their
            average price for the stay duration.

        Query Details :

            SELECT new HotelPriceDto(i.hotel, AVG(i.price))
                -> Creates HotelPriceDto directly

            FROM HotelMinPrice i
                -> Searches HotelMinPrice table

            WHERE i.hotel.city = :city
                -> Hotel must belong to requested city

            AND i.date BETWEEN :startDate AND :endDate
                -> Price records must exist for requested dates

            AND i.hotel.active = true
                -> Only active hotels are considered

            GROUP BY i.hotel
                -> Groups records hotel-wise

            AVG(i.price)
                -> Calculates average hotel price
                   for the requested stay duration

        Search Flow :

            City + Dates
                    ↓
            HotelMinPrice
                    ↓
            Average Price
                    ↓
            HotelPriceDto
                    ↓
            API Response

        Note :
            - Uses pre-calculated pricing data.
            - Avoids scanning Inventory table.
            - Improves hotel search performance.
            - Returns HotelPriceDto projection directly.
*/

/*
    findByHotelAndDate Query :

        Purpose :
            Finds minimum price record for a hotel
            on a specific date.

        Logic :
            1. Filter by hotel.
            2. Filter by date.
            3. Return matching HotelMinPrice record.

        Example :

            Hotel = Taj Hotel
            Date  = 10 Jun 2026

            Result :

                HotelMinPrice
                    Hotel = Taj Hotel
                    Date  = 10 Jun 2026
                    Price = ₹575

        Business Use :
            - Used by pricing scheduler.
            - Updates existing HotelMinPrice records.
            - Prevents duplicate HotelMinPrice entries.

        Note :
            - Derived query generated by Spring Data JPA.
            - No custom JPQL query required.
*/