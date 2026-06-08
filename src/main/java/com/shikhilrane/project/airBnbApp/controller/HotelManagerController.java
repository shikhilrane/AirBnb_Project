package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.BookingDto;
import com.shikhilrane.project.airBnbApp.dto.HotelDto;
import com.shikhilrane.project.airBnbApp.dto.HotelReportDto;
import com.shikhilrane.project.airBnbApp.service.BookingService;
import com.shikhilrane.project.airBnbApp.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/hotels")
public class HotelManagerController {

    private final HotelService hotelService;            // Handles hotel management operations
    private final BookingService bookingService;        // Handles booking and reporting operations

    // Creates a new hotel
    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@Valid @RequestBody HotelDto hotelDto) {
        HotelDto newHotel = hotelService.createNewHotel(hotelDto);              // Creates hotel record
        return new ResponseEntity<>(newHotel, HttpStatus.CREATED);              // Returns created hotel with 201 status
    }

    // Retrieves hotel details by identifier
    @GetMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable(name = "hotelId") Long id) {
        HotelDto hotelById = hotelService.getHotelById(id);                     // Fetches hotel information
        return ResponseEntity.ok(hotelById);                                    // Returns hotel details
    }

    // Updates an existing hotel
    @PutMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDto> updateHotel(@Valid @RequestBody HotelDto hotelDto,
                                                @PathVariable(name = "hotelId") Long id) {

        HotelDto hotelToUpdate = hotelService.updateHotelById(id, hotelDto);    // Updates hotel information
        return ResponseEntity.ok(hotelToUpdate);                                // Returns updated hotel
    }

    // Deletes a hotel by identifier
    @DeleteMapping(path = "/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable(name = "hotelId") Long id) {
        hotelService.deleteHotelById(id);                                       // Removes hotel from system
        return ResponseEntity.noContent().build();                              // Returns successful deletion response
    }

    // Activates a hotel and makes it available for bookings
    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId) {
        hotelService.activateHotel(hotelId);                                    // Changes hotel status to active
        return ResponseEntity.noContent().build();                              // Returns successful activation response
    }

    // Retrieves all hotels
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());                  // Returns all available hotels
    }

    // Retrieves all bookings associated with a hotel
    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookingsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(
                bookingService.getAllBookingsByHotelId(hotelId)                // Fetches hotel booking history
        );
    }

    // Generates booking and revenue report for a hotel
    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId, @RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) {

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);         // Defaults to previous month
        }

        if (endDate == null) {
            endDate = LocalDate.now();                                          // Defaults to current date
        }

        return ResponseEntity.ok(
                bookingService.getHotelReport(hotelId, startDate, endDate)      // Returns hotel performance report
        );
    }
}

/*
    HotelManagerController

        Purpose :
            Handles hotel management operations for hotel managers.
            Acts as the entry point for hotel administration APIs.

        Responsibilities :
            - Create hotels
            - Retrieve hotel details
            - Update hotel information
            - Delete hotels
            - Activate hotels
            - Retrieve hotel bookings
            - Generate hotel reports

        Endpoints :

            POST /hotels
                - Creates a new hotel

            GET /hotels/{hotelId}
                - Retrieves hotel details

            PUT /hotels/{hotelId}
                - Updates hotel information

            DELETE /hotels/{hotelId}
                - Deletes a hotel

            PATCH /hotels/{hotelId}/activate
                - Activates a hotel

            GET /hotels
                - Retrieves all hotels

            GET /hotels/{hotelId}/bookings
                - Retrieves hotel bookings

            GET /hotels/{hotelId}/reports
                - Generates hotel report

        Hotel Lifecycle :

            Hotel Created
                    ↓
                INACTIVE
                    ↓
            Hotel Activation
                    ↓
                 ACTIVE
                    ↓
            Available For Booking

        Booking Monitoring Flow :

            Hotel Manager
                    ↓
            View Bookings
                    ↓
            Booking Service
                    ↓
            Booking Details Returned

        Reporting Flow :

            Hotel Manager
                    ↓
            Request Report
                    ↓
            Date Range Validation
                    ↓
            Booking Analysis
                    ↓
            Revenue & Booking Report
                    ↓
            API Response

        Report Parameters :

            startDate
                - Optional
                - Defaults to one month before current date

            endDate
                - Optional
                - Defaults to current date

        Business Use :
            - Hotel onboarding
            - Hotel administration
            - Inventory activation
            - Booking management
            - Performance monitoring
            - Revenue tracking
            - Business reporting

        Security Features :
            - Request validation
            - Role-based access control
            - Hotel ownership validation
            - Controlled activation workflow

        Note :
            - Newly created hotels may remain inactive until activated.
            - Reports support custom date ranges.
            - Default report period is the last one month.
            - Controller delegates all business logic to services.
            - Hotel activation is performed separately from creation.

        This controller acts as the central management
        interface for hotel administration operations.
*/