package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.BookingDto;
import com.shikhilrane.project.airBnbApp.dto.BookingRequest;
import com.shikhilrane.project.airBnbApp.dto.GuestDto;
import com.shikhilrane.project.airBnbApp.entity.*;
import com.shikhilrane.project.airBnbApp.entity.enums.BookingStatus;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.exception.UnAuthorisedException;
import com.shikhilrane.project.airBnbApp.repository.*;
import com.shikhilrane.project.airBnbApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final HotelRepository hotelRepository;          // Performs hotel database operations
    private final RoomRepository roomRepository;            // Performs room database operations
    private final InventoryRepository inventoryRepository;  // Performs inventory database operations
    private final BookingRepository bookingRepository;      // Performs booking database operations
    private final GuestRepository guestRepository;          // Performs guest database operations
    private final ModelMapper modelMapper;                  // Converts DTOs into entities and vice versa

    // Creates a new booking and reserves room inventory
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

        // Verifies inventory availability for all stay dates
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

        // Creates booking entity
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

    // Adds guests to an existing booking
    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding guests for booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));                // Fetches booking or throws exception
        User user = getCurrentUser();                                                                   // Fetches currently logged-in user from Security Context

        // Verifies that booking belongs to the current user
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId()); // Prevents users from accessing bookings owned by other users
        }

        // Checks whether booking reservation has expired
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");                             // Prevents adding guests to expired booking
        }

        // Allows guest addition only when booking is in RESERVED state
        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");  // Allows guest addition only for RESERVED bookings
        }

        // Adds all guests to the booking
        for (GuestDto guestDto : guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);                    // Converts DTO to Entity
            guest.setUser(user);                                                     // Associates guest with current user
            guest = guestRepository.save(guest);                                     // Saves guest
            booking.getGuests().add(guest);                                          // Associates guest with booking
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);                        // Updates booking status

        booking = bookingRepository.save(booking);                                   // Saves updated booking

        return modelMapper.map(booking, BookingDto.class);                           // Converts Booking to DTO
    }

    // Checks whether reservation has expired
    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now());                                      // Checks if reservation expired after 10 minutes
    }

    // Returns currently logged-in user
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

/*
    BookingServiceImpl

        Purpose : Handles booking-related business operations.

        Responsibilities :
            - Create bookings
            - Reserve room inventory
            - Add guests to bookings
            - Validate booking ownership
            - Check booking expiration

        Methods :

            initialiseBooking()
                - Validates hotel and room
                - Checks inventory availability
                - Locks inventory records
                - Reserves rooms
                - Creates booking

            addGuests()
                - Validates booking ownership
                - Checks booking expiry
                - Adds guests to booking
                - Updates booking status

            hasBookingExpired()
                - Checks whether booking expired
                - Reservation expires after 10 minutes

            getCurrentUser()
                - Returns authenticated user
                - Reads user from Security Context

        Booking Flow :

            Search Hotel
                    ↓
              Select Room
                    ↓
            Initialise Booking
                    ↓
             Inventory Locked
                    ↓
            Rooms Reserved
                    ↓
             Booking Created
                    ↓
                RESERVED
                    ↓
               Add Guests
                    ↓
             GUESTS_ADDED

        Guest Addition Flow :

            Booking Selected
                    ↓
            Ownership Check
                    ↓
             Expiry Check
                    ↓
               Add Guests
                    ↓
            Booking Updated

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

        Business Use :
            - Hotel booking management
            - Guest management
            - Inventory reservation
            - Booking validation
            - Overbooking prevention

        Note :
            - Uses PESSIMISTIC_WRITE locking.
            - Runs inside database transactions.
            - Guests can only be added to RESERVED bookings.
            - Reservation expires after 10 minutes.

        This class acts as the booking management service
        of the application.
*/