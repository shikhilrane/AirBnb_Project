package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.BookingDto;
import com.shikhilrane.project.airBnbApp.dto.BookingRequest;
import com.shikhilrane.project.airBnbApp.dto.GuestDto;
import com.shikhilrane.project.airBnbApp.entity.*;
import com.shikhilrane.project.airBnbApp.entity.enums.BookingStatus;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.repository.*;
import com.shikhilrane.project.airBnbApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {
        log.info("Initialising booking for hotel : {}, room: {}, date {}-{}", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());     // Logs booking initialization request

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() ->
                new ResourceNotFoundException("Hotel not found with id: "+bookingRequest.getHotelId()));            // Fetches hotel or throws exception

        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(() ->
                new ResourceNotFoundException("Room not found with id: "+bookingRequest.getRoomId()));              // Fetches room or throws exception

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount()); // Fetches and locks available inventory

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())+1; // Calculates total stay duration

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore");                                       // Ensures inventory exists for all stay dates
        }

        // Reserve the room/ update the booked count of inventories

        for(Inventory inventory: inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());              // Reserves requested rooms
        }

        inventoryRepository.saveAll(inventoryList);                                                                 // Saves updated inventory records

        // Create Booking

        User user = new User();                                                                                     // Create temporary dummy user
        user.setId(1L);

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)                               // Sets booking status as RESERVED
                .hotel(hotel)                                                        // Associates hotel
                .room(room)                                                          // Associates room
                .checkInDate(bookingRequest.getCheckInDate())                        // Sets check-in date
                .checkOutDate(bookingRequest.getCheckOutDate())                      // Sets check-out date
                .user(user)                                                          // Associates booking user
                .roomsCount(bookingRequest.getRoomsCount())                          // Sets room count
                .amount(BigDecimal.TEN)                                              // Sets booking amount (temporary)
                .build();

        booking = bookingRepository.save(booking);                                   // Saves booking

        return modelMapper.map(booking, BookingDto.class);                           // Converts Booking to DTO
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding guests for booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));                // Fetches booking or throws exception

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");                             // Prevents adding guests to expired booking
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");  // Allows guest addition only for RESERVED bookings
        }

        for (GuestDto guestDto : guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);                    // Converts DTO to Entity
            guest.setUser(getCurrentUser());                                         // Associates guest with current user
            guest = guestRepository.save(guest);                                     // Saves guest
            booking.getGuests().add(guest);                                          // Associates guest with booking
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);                        // Updates booking status

        booking = bookingRepository.save(booking);                                   // Saves updated booking

        return modelMapper.map(booking, BookingDto.class);                           // Converts Booking to DTO
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now());                                      // Checks if reservation expired after 10 minutes
    }

    public User getCurrentUser() {
        User user = new User();
        user.setId(1L);                                                              // Temporary dummy user
        return user;
    }
}

/*
    BookingServiceImpl

        Purpose : Handles booking-related business operations.
                  Manages booking creation, guest addition,
                  inventory reservation, and booking validation.

        Responsibilities :
            - Initialize a booking
            - Reserve room inventory
            - Add guests to booking
            - Validate booking status
            - Check booking expiration

        Methods :

            initialiseBooking()
                - Validates hotel and room
                - Checks room availability
                - Locks inventory records
                - Reserves inventory
                - Creates booking

            addGuests()
                - Validates booking existence
                - Checks booking expiry
                - Adds guests to booking
                - Updates booking status

            hasBookingExpired()
                - Checks whether booking reservation expired

            getCurrentUser()
                - Returns current logged-in user
                - Currently uses dummy user

        Business Use :
            - Prevents overbooking using inventory locks
            - Reserves rooms before payment
            - Associates guests with bookings
            - Controls booking lifecycle

        Booking Flow :

            Search Hotel
                    ↓
              Select Room
                    ↓
            Initialise Booking
                    ↓
             Inventory Locked
                    ↓
            Inventory Reserved
                    ↓
             Booking Created
                    ↓
                RESERVED
                    ↓
               Add Guests
                    ↓
             GUESTS_ADDED

        Booking Expiry Flow :

            Booking Created
                    ↓
                RESERVED
                    ↓
               10 Minutes
                    ↓
             Not Completed
                    ↓
                EXPIRED

        Inventory Reservation :

            Available Rooms
                =
            Total Rooms
                -
            Booked Rooms
                -
            Reserved Rooms

        Concurrency Protection :

            User A Books Room
                    ↓
             Inventory Locked
                    ↓
             User B Waits
                    ↓
          Prevents Overbooking

        Note :
            - Inventory records are locked using
              PESSIMISTIC_WRITE.
            - Booking creation runs inside a transaction.
            - Guests can only be added to RESERVED bookings.
            - Reservation expires after 10 minutes.
            - Current user retrieval uses dummy data
              and should be replaced with Spring Security.

        This class acts as the business layer
        for booking management.
*/