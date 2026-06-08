package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.BookingDto;
import com.shikhilrane.project.airBnbApp.dto.BookingRequest;
import com.shikhilrane.project.airBnbApp.dto.GuestDto;
import com.shikhilrane.project.airBnbApp.dto.HotelReportDto;
import com.shikhilrane.project.airBnbApp.entity.*;
import com.shikhilrane.project.airBnbApp.entity.enums.BookingStatus;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.exception.UnAuthorisedException;
import com.shikhilrane.project.airBnbApp.repository.*;
import com.shikhilrane.project.airBnbApp.service.BookingService;
import com.shikhilrane.project.airBnbApp.service.CheckoutService;
import com.shikhilrane.project.airBnbApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

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
    private final CheckoutService checkoutService;          // Creates Stripe checkout sessions
    private final PricingService pricingService;            // Calculates dynamic booking pricing

    @Value("${frontend.url}")
    private String frontendUrl;                             // Frontend redirect URL after payment success/failure

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

        if (!room.getHotel().getId().equals(hotel.getId())) {
            throw new IllegalStateException("Room does not belong to this hotel");
        }

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount()); // Fetches and locks available inventory

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())+1; // Calculates total stay duration

        // Verifies inventory availability for all stay dates
        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore");                                       // Ensures inventory exists for all stay dates
        }

        // Reserve the room/ update the booked count of inventories
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        // Create Booking
        User user = getCurrentUser();

        // Calculate the final pricing for the booking we initiated
        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);       // Calculates dynamic price for a single room across all booked dates
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount())); // Calculates final booking amount based on room count

        // Creates booking entity
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)                               // Sets booking status as RESERVED
                .hotel(hotel)                                                        // Associates hotel
                .room(room)                                                          // Associates room
                .checkInDate(bookingRequest.getCheckInDate())                        // Sets check-in date
                .checkOutDate(bookingRequest.getCheckOutDate())                      // Sets check-out date
                .user(user)                                                          // Associates booking user
                .roomsCount(bookingRequest.getRoomsCount())                          // Sets room count
                .amount(totalPrice)                                                  // Sets booking amount
                .build();

        booking = bookingRepository.save(booking);                                   // Saves booking

        return modelMapper.map(booking, BookingDto.class);                           // Converts Booking to DTO
    }

    // Adds guests to an existing booking
    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<Long> guestIds) {
        log.info("Adding guests for booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        User user = getCurrentUser();

        if (!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: " + user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state");
        }

        for (Long guestId : guestIds) {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id: " + guestId));

            if (!guest.getUser().getId().equals(user.getId())) {
                throw new UnAuthorisedException("Guest does not belong to current user");
            }

            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    // Creates Stripe checkout session and moves booking to payment pending state
    @Override
    public String initiatePayments(Long bookingId) {

        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: " + bookingId)); // Fetches booking or throws exception

        User user = getCurrentUser(); // Retrieves currently authenticated user

        // Verifies that booking belongs to the current user
        if (!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorisedException(
                    "Booking does not belong to this user with id: " + user.getId());
        }

        // Prevents payment for expired reservations
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException(
                    "Booking has already expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(
                booking,
                frontendUrl + "/payments/success",
                frontendUrl + "/payments/failure"
        ); // Creates Stripe checkout session and returns payment URL

        booking.setBookingStatus(
                BookingStatus.PAYMENTS_PENDING
        ); // Marks booking as awaiting payment completion

        bookingRepository.save(booking); // Saves updated booking status

        return sessionUrl; // Returns Stripe checkout URL to client
    }

    // Handles Stripe webhook events and updates booking status accordingly
    @Override
    @Transactional
    public void capturePayment(Event event) {

        log.info("Received Stripe Event: {}", event.getType()); // Logs incoming Stripe webhook event

        // Handles successful payment completion event
        if ("checkout.session.completed".equals(event.getType())) {

            try {

                Session session =
                        (Session) event.getDataObjectDeserializer().deserializeUnsafe(); // Extracts checkout session from webhook payload

                log.info("Session ID: {}", session.getId());

                String sessionId = session.getId(); // Retrieves Stripe checkout session ID

                Booking booking =
                        bookingRepository.findByPaymentSessionId(sessionId)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "Booking not found for session ID: " + sessionId)); // Fetches booking associated with payment session

                booking.setBookingStatus(BookingStatus.CONFIRMED); // Marks booking as successfully confirmed

                bookingRepository.save(booking); // Saves updated booking status

                inventoryRepository.findAndLockReservedInventory(
                        booking.getRoom().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getRoomsCount()); // Locks inventory records before confirmation

                inventoryRepository.confirmBooking(
                        booking.getRoom().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getRoomsCount()); // Moves reserved inventory into confirmed inventory

                log.info("Successfully confirmed booking {}", booking.getId());

            } catch (Exception e) {

                log.error("Stripe webhook error", e); // Logs payment confirmation failure

            }

        }

        // Handles checkout sessions that expired before payment completion
        else if ("checkout.session.expired".equals(event.getType())) {

            try {

                Session session =
                        (Session) event.getDataObjectDeserializer().deserializeUnsafe(); // Extracts expired checkout session

                String sessionId = session.getId(); // Retrieves expired session ID

                Booking booking =
                        bookingRepository.findByPaymentSessionId(sessionId)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "Booking not found for session ID: " + sessionId)); // Fetches booking linked to expired session

                booking.setBookingStatus(BookingStatus.CANCELLED); // Marks booking as cancelled

                bookingRepository.save(booking); // Saves updated booking status

                inventoryRepository.cancelBooking(
                        booking.getRoom().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getRoomsCount()
                ); // Releases previously reserved inventory

                log.info(
                        "Booking cancelled due to expired checkout session. Booking ID: {}",
                        booking.getId()
                );

            } catch (Exception e) {

                log.error(
                        "Stripe checkout.session.expired webhook error",
                        e
                ); // Logs session expiration handling failure

            }

        }

        // Handles unsupported Stripe webhook events
        else {

            log.warn("Unhandled event type: {}", event.getType());

        }
    }

    // Cancels a confirmed booking, releases inventory and initiates refund
    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found with id: " + bookingId)); // Fetches booking or throws exception

        User user = getCurrentUser(); // Retrieves currently authenticated user

        // Verifies that booking belongs to the current user
        if (!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorisedException(
                    "Booking does not belong to this user with id: " + user.getId());
        }

        // Allows cancellation only for confirmed bookings
        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED); // Marks booking as cancelled

        bookingRepository.save(booking); // Persists updated booking status

        inventoryRepository.findAndLockReservedInventory(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount()); // Locks inventory rows before update

        inventoryRepository.cancelBooking(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount()); // Releases booked inventory back to availability

        // Initiates refund through Stripe
        try {

            Session session =
                    Session.retrieve(booking.getPaymentSessionId()); // Retrieves Stripe checkout session

            RefundCreateParams refundParams =
                    RefundCreateParams.builder()
                            .setPaymentIntent(session.getPaymentIntent()) // Uses payment intent associated with booking payment
                            .build();

            Refund.create(refundParams); // Creates refund in Stripe

        } catch (StripeException e) {

            throw new RuntimeException(e); // Propagates Stripe refund failure

        }
    }

    // Returns current status of a booking after ownership validation
    @Override
    public BookingStatus getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: "+bookingId));

        User user = getCurrentUser();

        if (!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus();     // Returns current booking status
    }

    // Retrieves all bookings associated with a hotel
    @Override
    public List<BookingDto> getAllBookingsByHotelId(Long hotelId) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        User user = getCurrentUser();

        log.info("Getting all booking for the hotel with ID: {}", hotelId);    // Logs booking retrieval request

        if(!user.getId().equals(hotel.getOwner().getId()))
            throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);

        List<Booking> bookings = bookingRepository.findByHotel(hotel);          // Retrieves hotel bookings

        return bookings.stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))   // Converts entities to DTOs
                .collect(Collectors.toList());
    }

    // Generates booking and revenue report for a hotel
    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        User user = getCurrentUser();

        log.info("Generating report for hotel with ID: {}", hotelId);           // Logs report generation request

        if(!user.getId().equals(hotel.getOwner().getId()))
            throw new AccessDeniedException("You are not the owner of hotel with id: "+hotelId);

        LocalDateTime startDateTime = startDate.atStartOfDay();                 // Converts start date to start of day
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);              // Converts end date to end of day

        List<Booking> bookings = bookingRepository.findByHotelAndCreatedAtBetween(
                hotel, startDateTime, endDateTime);                             // Retrieves bookings within date range

        Long totalConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();                                                       // Counts confirmed bookings

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);                      // Calculates total revenue

        BigDecimal avgRevenue = totalConfirmedBookings == 0
                ? BigDecimal.ZERO
                : totalRevenueOfConfirmedBookings.divide(
                BigDecimal.valueOf(totalConfirmedBookings),
                RoundingMode.HALF_UP
        );                                                              // Calculates average revenue

        return new HotelReportDto(
                totalConfirmedBookings,
                totalRevenueOfConfirmedBookings,
                avgRevenue
        );
    }

    // Retrieves booking history of the currently authenticated user
    @Override
    public List<BookingDto> getMyBookings() {

        User user = getCurrentUser();                                           // Retrieves logged-in user

        return bookingRepository.findByUser(user)
                .stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))   // Converts bookings to DTOs
                .collect(Collectors.toList());
    }

    // Checks whether reservation has expired
    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt()
                .plusMinutes(10)
                .isBefore(LocalDateTime.now());                                 // Reservation expires after 10 minutes
    }

    // Returns currently authenticated user from Spring Security context
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();                                                // Extracts authenticated user
    }
}

/*
    BookingServiceImpl

        Purpose :
            Handles complete booking lifecycle management.
            Manages reservations, guests, payments, cancellations and booking status.

        Responsibilities :
            - Create bookings
            - Reserve room inventory
            - Calculate dynamic booking price
            - Add guests to bookings
            - Initiate Stripe payments
            - Confirm bookings after successful payment
            - Cancel bookings
            - Process refunds
            - Validate booking ownership
            - Check booking expiration
            - Handle Stripe webhook events
            - Generate hotel booking reports

        Methods :

            initialiseBooking()
                - Validates hotel and room
                - Verifies room belongs to hotel
                - Checks inventory availability
                - Locks inventory records
                - Reserves inventory
                - Calculates dynamic pricing
                - Creates booking

            addGuests()
                - Validates booking ownership
                - Checks booking expiry
                - Adds guests to booking
                - Updates booking status

            initiatePayments()
                - Validates booking ownership
                - Checks booking expiry
                - Creates Stripe checkout session
                - Updates booking status

            capturePayment()
                - Handles Stripe webhook events
                - Confirms successful payments
                - Releases inventory for expired sessions
                - Updates booking status

            cancelBooking()
                - Validates booking ownership
                - Cancels confirmed booking
                - Releases inventory
                - Initiates refund

            getBookingStatus()
                - Returns booking status

            getAllBookingsByHotelId()
                - Retrieves hotel bookings
                - Validates hotel ownership

            getHotelReport()
                - Generates booking and revenue report

            getMyBookings()
                - Retrieves current user's bookings

            hasBookingExpired()
                - Checks whether booking expired
                - Reservation expires after 10 minutes

            getCurrentUser()
                - Returns authenticated user
                - Reads user from Security Context

        Booking Lifecycle :

            Booking Created
                    ↓
                RESERVED
                    ↓
               Add Guests
                    ↓
              GUESTS_ADDED
                    ↓
            Initiate Payment
                    ↓
            PAYMENTS_PENDING
                    ↓
          Stripe Payment Success
                    ↓
                CONFIRMED

        Reservation Expiry Flow :

                RESERVED
                    ↓
       No Action For 10 Minutes
                    ↓
            Reservation Expired
                    ↓
          Payment Not Allowed

        Stripe Expiry Flow :

            PAYMENTS_PENDING
                    ↓
        checkout.session.expired
                    ↓
                CANCELLED
                    ↓
           Inventory Released

        Cancellation Flow :

                CONFIRMED
                    ↓
             Cancel Booking
                    ↓
            Inventory Released
                    ↓
             Refund Created
                    ↓
                CANCELLED

        Payment Flow :

            Booking Created
                    ↓
          Stripe Checkout Session
                    ↓
              User Payment
                    ↓
            Stripe Webhook
                    ↓
            capturePayment()
                    ↓
                CONFIRMED

        Dynamic Pricing Flow :

            Inventory List
                    ↓
              PricingService
                    ↓
            Surge Pricing
                    ↓
          Occupancy Pricing
                    ↓
            Urgency Pricing
                    ↓
            Holiday Pricing
                    ↓
              Final Amount

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

        Stripe Webhook Events :

            checkout.session.completed
                    ↓
                CONFIRMED

            checkout.session.expired
                    ↓
                CANCELLED

        Hotel Reporting Flow :

            Hotel Manager
                    ↓
            Request Report
                    ↓
          Booking Repository
                    ↓
          Revenue Calculation
                    ↓
            HotelReportDto

        Business Use :
            - Hotel booking management
            - Guest management
            - Dynamic pricing
            - Inventory reservation
            - Payment processing
            - Refund processing
            - Booking tracking
            - Hotel revenue reporting
            - Overbooking prevention

        Security Features :
            - Booking ownership validation
            - Inventory locking
            - Transaction management
            - Secure refund processing
            - Access control validation

        Note :
            - Uses PESSIMISTIC_WRITE locking.
            - Runs inside database transactions.
            - Dynamic pricing is calculated using PricingService.
            - Guests can only be added to RESERVED bookings.
            - Reservation expires after 10 minutes.
            - Payment processing is handled by Stripe Checkout.
            - Booking confirmation happens through webhooks.
            - Expired payment sessions automatically release inventory.
            - Refunds are processed through Stripe.

        This class acts as the central booking,
        payment, cancellation, and reporting
        service of the application.
*/