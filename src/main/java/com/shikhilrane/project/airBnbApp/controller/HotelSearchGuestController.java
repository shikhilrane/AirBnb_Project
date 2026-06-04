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
@RequestMapping(path = "/hotels")
@Validated
public class HotelSearchGuestController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    // 1. Search hotels based on city, dates, room count, and pricing
    @GetMapping(path = "/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@Valid @RequestBody HotelSearchReuestDto hotelSearchReuestDto) {
        var page = inventoryService.searchHotels(hotelSearchReuestDto);   // Searches hotels and returns pricing information
        return ResponseEntity.ok(page);                                   // Returns paginated hotel list with prices
    }

    // 2. Get complete hotel information by hotel ID
    @GetMapping(path = "/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));                                           // Fetches detailed hotel information
    }
}

/*
    HotelSearchGuestController

        Purpose : Handles hotel search and hotel information APIs for guest users.

        Responsibilities :
            - Search available hotels
            - View hotel details
            - Return hotel pricing information

        Endpoints :

            GET /hotels/search
                - Searches hotels based on:
                    • City
                    • Check-in date
                    • Check-out date
                    • Required room count
                - Returns hotel details with average pricing

            GET /hotels/{hotelId}/info
                - Returns complete hotel information
                - Returns room details

        Flow :

            Guest Request
                  ↓
            Controller
                  ↓
            Service Layer
                  ↓
            Repository Layer
                  ↓
            Database

        Example :

            Search Hotels
                GET /hotels/search

            Response :

                Hotel Name : Hotel Lotus
                Price      : ₹575

            Get Hotel Details
                GET /hotels/1/info

        Business Use :
            - Allows guests to search hotels.
            - Displays hotel pricing information.
            - Displays hotel and room details.
            - Supports hotel booking flow.

        Search Flow :

            City + Dates + Rooms
                    ↓
            Hotel Search
                    ↓
            HotelMinPrice
                    ↓
            HotelPriceDto
                    ↓
            API Response

        Note :
            - Search results are paginated.
            - Only active hotels are returned.
            - Pricing is fetched from HotelMinPrice table.
            - Controller contains no business logic.

        This controller is responsible for hotel search
        and hotel information APIs.
*/