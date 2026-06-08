package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.HotelDto;
import com.shikhilrane.project.airBnbApp.dto.HotelInfoDto;
import com.shikhilrane.project.airBnbApp.dto.HotelPriceDto;
import com.shikhilrane.project.airBnbApp.dto.HotelSearchReuestDto;
import com.shikhilrane.project.airBnbApp.service.HotelService;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/searchHotels")
@Validated
public class HotelSearchGuestController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    // Searches hotels based on city, dates, room count, and pricing
    @GetMapping(path = "/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@Valid @RequestBody HotelSearchReuestDto hotelSearchReuestDto) {
        var page = inventoryService.searchHotels(hotelSearchReuestDto);   // Searches hotels and calculates pricing information
        return ResponseEntity.ok(page);                                   // Returns paginated hotel search results
    }

    // Retrieves complete hotel information by hotel ID
    @GetMapping(path = "/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId)); // Fetches detailed hotel and room information
    }
}

/*
    HotelSearchGuestController

        Purpose :
            Handles hotel search and hotel information APIs for guest users.

        Responsibilities :
            - Search available hotels
            - View hotel details
            - View hotel pricing information
            - Support hotel booking discovery

        Endpoints :

            GET /searchHotels/search
                - Searches hotels based on:
                    • City
                    • Check-in date
                    • Check-out date
                    • Required room count
                    • Guest requirements
                - Returns hotels with pricing information

            GET /searchHotels/{hotelId}/info
                - Returns complete hotel information
                - Returns room details
                - Returns hotel metadata

        Hotel Search Flow :

            Guest Search Request
                    ↓
            Search Criteria Validation
                    ↓
            Inventory Availability Check
                    ↓
            Hotel Matching
                    ↓
            Price Calculation
                    ↓
            Paginated Results
                    ↓
            API Response

        Hotel Information Flow :

            Guest Requests Hotel Details
                        ↓
            Hotel Lookup
                        ↓
            Room Information Fetch
                        ↓
            Hotel Information DTO
                        ↓
            API Response

        Search Parameters :

            City
                - Destination city

            Check-In Date
                - Booking start date

            Check-Out Date
                - Booking end date

            Room Count
                - Number of rooms required

        Business Use :
            - Hotel discovery
            - Price comparison
            - Booking planning
            - Room availability checking
            - Hotel information browsing

        Security Features :
            - Request validation
            - Controlled data exposure
            - Availability-based search results

        Note :
            - Search results are paginated.
            - Only eligible hotels are returned.
            - Pricing information is included in search results.
            - Detailed hotel information is available through a separate endpoint.
            - Controller delegates all business logic to service layers.

        This controller acts as the primary
        hotel discovery and search entry point
        for guest users.
*/