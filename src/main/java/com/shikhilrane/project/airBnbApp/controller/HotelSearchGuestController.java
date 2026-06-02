package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.HotelDto;
import com.shikhilrane.project.airBnbApp.dto.HotelInfoDto;
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

    // 1. Search hotels based on city, dates, and room count
    @GetMapping(path = "/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@Valid @RequestBody HotelSearchReuestDto hotelSearchReuestDto){
        Page<HotelDto> page = inventoryService.searchHotels(hotelSearchReuestDto);                                  // Searches available hotels
        return ResponseEntity.ok(page);                                                                             // Returns paginated hotel list
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

        Endpoints :

            GET /hotels/search
                - Searches hotels based on:
                    • City
                    • Check-in date
                    • Check-out date
                    • Required room count

            GET /hotels/{hotelId}/info
                - Returns complete hotel information

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

            Get Hotel Details
                GET /hotels/1/info

        Business Use :
            - Allows guests to find available hotels
            - Displays hotel details before booking
            - Supports hotel discovery and booking flow

        Note :
            - Search results are returned in paginated format.
            - Only hotels with available inventory are returned.
            - Controller contains no business logic.

        This controller is responsible for hotel discovery features.
*/