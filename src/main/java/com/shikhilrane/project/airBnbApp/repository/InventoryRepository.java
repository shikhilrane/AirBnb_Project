package com.shikhilrane.project.airBnbApp.repository;

import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Inventory;
import com.shikhilrane.project.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Deletes all inventory records associated with a room
    void deleteByRoom(Room room);

    // Finds hotels with enough available rooms for the complete stay duration
    @Query("""
            SELECT DISTINCT i.hotel
            FROM Inventory i
            WHERE i.city = :city
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
           GROUP BY i.hotel, i.room
           HAVING COUNT(i.date) = :dateCount
           """)
    Page<Hotel> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    // Finds and locks inventory records before booking creation
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    // Fetches inventory records of a hotel for a given date range
    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);

    // Finds and locks reserved inventory before booking confirmation or cancellation
    @Query("""
                SELECT i
                FROM Inventory i
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                  AND i.closed = false
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(@Param("roomId") Long roomId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("numberOfRooms") int numberOfRooms);

    // Moves reserved inventory into confirmed bookings after successful payment
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount - :numberOfRooms,
                    i.bookedCount = i.bookedCount + :numberOfRooms
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                  AND i.reservedCount >= :numberOfRooms
                  AND i.closed = false
            """)
    void confirmBooking(@Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms);

    // Releases booked inventory when a confirmed booking is cancelled
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.bookedCount = i.bookedCount - :numberOfRooms
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                  AND i.closed = false
            """)
    void cancelBooking(@Param("roomId") Long roomId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("numberOfRooms") int numberOfRooms);

    // Reserves inventory during booking initialization before payment completion
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount + :numberOfRooms
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= :numberOfRooms
                  AND i.closed = false
            """)
    void initBooking(@Param("roomId") Long roomId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                     @Param("numberOfRooms") int numberOfRooms);
}

/*
    InventoryRepository

        Purpose :
            Provides database operations
            for Inventory records.

        Responsibilities :
            - Save inventory records
            - Delete inventory records
            - Search hotel availability
            - Lock inventory during booking
            - Reserve inventory
            - Confirm inventory
            - Release inventory
            - Prevent overbooking
            - Fetch inventory for pricing updates

        Methods :

            deleteByRoom()
                - Deletes all inventory records
                  associated with a room

            findHotelsWithAvailableInventory()
                - Finds hotels having available rooms
                - Checks availability for complete stay duration
                - Returns paginated hotel list

            findAndLockAvailableInventory()
                - Finds available inventory records
                - Locks inventory during booking
                - Prevents overbooking

            findByHotelAndDateBetween()
                - Fetches inventory records
                  for a hotel and date range
                - Used by pricing scheduler

            findAndLockReservedInventory()
                - Finds reserved inventory records
                - Locks inventory before confirmation
                - Locks inventory before cancellation

            confirmBooking()
                - Converts reserved rooms into booked rooms
                - Called after successful payment

            cancelBooking()
                - Releases booked rooms
                - Called during booking cancellation

            initBooking()
                - Reserves rooms during booking initialization
                - Executed before payment completion

        Inventory Lifecycle :

            Available Room
                    ↓
              initBooking()
                    ↓
               Reserved
                    ↓
         Payment Success
                    ↓
            confirmBooking()
                    ↓
                Booked

            OR

               Reserved
                    ↓
        Session Expired
                    ↓
             Cancelled
                    ↓
             Available

            OR

                Booked
                    ↓
           cancelBooking()
                    ↓
             Available

        Booking Flow :

            Search Room
                    ↓
         Lock Inventory
                    ↓
          Reserve Room
                    ↓
          Stripe Payment
                    ↓
         Confirm Booking
                    ↓
             Stay Date

        Business Use :
            - Room availability management
            - Hotel search
            - Booking creation
            - Booking confirmation
            - Booking cancellation
            - Dynamic pricing support
            - Inventory reservation
            - Overbooking prevention

        Concurrency Protection :

            User A Books Room
                    ↓
             Inventory Locked
                    ↓
             User B Waits
                    ↓
          Prevents Overbooking

        Security Features :
            - Pessimistic row locking
            - Transaction-safe inventory updates
            - Concurrent booking protection
            - Consistent inventory management

        Note :
            - Uses PESSIMISTIC_WRITE locks.
            - Inventory updates run inside transactions.
            - Reserved inventory is confirmed after payment success.
            - Cancelled bookings release inventory.
            - Inventory remains consistent under concurrent requests.

        This repository acts as the data access layer
        for inventory availability and booking management.
*/

/*
    findHotelsWithAvailableInventory Query :

        Purpose :
            Finds hotels having enough available rooms for every day of the requested stay.

        Logic :
            1. Filter by city.
            2. Filter by date range.
            3. Ignore closed inventory.
            4. Check room availability.
            5. Ensure availability exists for all stay dates.
            6. Return unique hotels.

        Availability Formula :
            Available Rooms =
                Total Rooms
                - Booked Rooms
                - Reserved Rooms

        Example :

            Search :
                City       = Mumbai
                Check-In   = 10 Jun
                Check-Out  = 12 Jun
                Rooms      = 2

            Required Dates :
                10 Jun
                11 Jun
                12 Jun

            Hotel Returned Only If :
                All 3 dates have at least
                2 available rooms.

        Result :
            Returns paginated list of hotels
            available for the complete stay duration.

        Query Details :
            SELECT DISTINCT i.hotel
                -> Selects unique hotels

            FROM Inventory i
                -> Searches inventory table

            WHERE i.city = :city
                -> Hotel must belong to requested city

            AND i.date BETWEEN :startDate AND :endDate
                -> Inventory must exist for requested date range

            AND i.closed = false
                -> Booking must be open for those dates

            AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
                -> Required number of rooms must be available

            GROUP BY i.hotel, i.room
                -> Groups inventory records hotel-wise and room-wise

            HAVING COUNT(i.date) = :dateCount
                -> Inventory must be available for every day of stay
*/



/*
    findAndLockAvailableInventory Query :

        Purpose :
            Finds available inventory records and locks them before creating a booking.

        Logic :
            1. Filter by room ID.
            2. Filter by date range.
            3. Ignore closed inventory.
            4. Check room availability.
            5. Lock matching inventory records.
            6. Return locked inventory records.

        Availability Formula :
            Available Rooms =
                Total Rooms
                - Booked Rooms
                - Reserved Rooms

        Example :
            Booking Request :
                Room ID    = 1
                Check-In   = 10 Jun
                Check-Out  = 12 Jun
                Rooms      = 2
            Required Dates :
                10 Jun
                11 Jun
                12 Jun
            Inventory Returned Only If :
                All 3 dates have at least 2 available rooms.

        Result :
            Returns inventory records for the requested stay
            and locks them for booking.

        Query Details :

            SELECT i
                -> Selects inventory records

            FROM Inventory i
                -> Searches inventory table

            WHERE i.room.id = :roomId
                -> Inventory must belong to requested room

            AND i.date BETWEEN :startDate AND :endDate
                -> Inventory must exist for requested date range

            AND i.closed = false
                -> Booking must be open for those dates

            AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
                -> Required number of rooms must be available

            @Lock(PESSIMISTIC_WRITE)
                -> Locks selected inventory records

        Why Lock Is Needed :
            User A and User B try booking
            the same room simultaneously.

            Without Lock :
                Both see room available
                Both create booking
                Overbooking occurs

            With Lock :
                User A locks inventory
                User B waits
                Inventory remains consistent

        Lock Type :
            PESSIMISTIC_WRITE
                - Acquires database write lock
                - Prevents concurrent updates
                - Avoids overbooking

        Note :
            - Used during booking initialization.
            - Must run inside a transaction.
            - Inventory remains locked until transaction completes.
*/

/*
    findByHotelAndDateBetween Query :

        Purpose :
            Fetches inventory records of a hotel
            within a given date range.

        Logic :
            1. Filter by hotel.
            2. Filter by date range.
            3. Return matching inventory records.

        Example :

            Hotel       = Taj Hotel
            Start Date  = 10 Jun
            End Date    = 15 Jun

            Result :
                Returns inventory records
                from 10 Jun to 15 Jun.

        Business Use :
            - Dynamic pricing updates
            - Inventory reporting
            - Availability management
            - Hotel operations

        Query Details :

            Hotel hotel
                -> Inventory must belong to hotel

            Date Between
                -> Inventory must fall
                   within requested date range

        Result :
            Returns all matching inventory records.

        Note :
            - No locking is applied.
            - Read-only operation.
            - Used by pricing scheduler.
*/

/*
    findAndLockReservedInventory Query :

        Purpose :
            Finds reserved inventory records and locks them
            before booking confirmation or cancellation.

        Logic :
            1. Filter by room ID.
            2. Filter by date range.
            3. Verify sufficient booked inventory exists.
            4. Lock matching inventory records.
            5. Return locked records.

        Example :

            Room ID    = 1
            Check-In   = 10 Jun
            Check-Out  = 12 Jun
            Rooms      = 2

            Result :
                Inventory records are locked
                before inventory modification.

        Query Details :

            SELECT i
                -> Selects inventory records

            WHERE i.room.id = :roomId
                -> Inventory belongs to room

            AND i.date BETWEEN :startDate AND :endDate
                -> Date range filter

            AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                -> Inventory consistency check

            AND i.closed = false
                -> Inventory must be open

            @Lock(PESSIMISTIC_WRITE)
                -> Locks rows for update

        Why Lock Is Needed :

            Payment Confirmation
                    OR
            Booking Cancellation

                    ↓

            Inventory Update

                    ↓

            Prevent Race Conditions

        Note :
            - Used before confirmBooking().
            - Used before cancelBooking().
            - Must execute inside transaction.
*/

/*
    confirmBooking Query :

        Purpose :
            Converts reserved inventory into booked inventory
            after successful payment.

        Logic :
            1. Reduce reservedCount.
            2. Increase bookedCount.
            3. Keep inventory totals consistent.

        Formula :

            Before :

                reservedCount = 5
                bookedCount   = 10

            After Confirm :

                reservedCount = 3
                bookedCount   = 12

        Query Details :

            SET i.reservedCount =
                i.reservedCount - :numberOfRooms

            SET i.bookedCount =
                i.bookedCount + :numberOfRooms

        Triggered By :

            Stripe Payment Success
                    ↓
        checkout.session.completed
                    ↓
            confirmBooking()

        Result :
            Inventory becomes permanently booked.

        Note :
            - Called after successful payment.
            - Converts temporary reservation into booking.
            - Prevents double booking.
*/

/*
    cancelBooking Query :

        Purpose :
            Releases booked inventory when a booking
            is cancelled.

        Logic :
            1. Find inventory records.
            2. Reduce bookedCount.
            3. Make rooms available again.

        Formula :

            Before :

                bookedCount = 15

            After Cancellation :

                bookedCount = 13

        Query Details :

            SET i.bookedCount =
                i.bookedCount - :numberOfRooms

        Triggered By :

            User Cancellation
                    ↓
            cancelBooking()

        Result :
            Rooms become available
            for future bookings.

        Business Use :
            - Booking cancellation
            - Refund workflow
            - Inventory recovery

        Note :
            - Used only for confirmed bookings.
            - Makes inventory available again.
*/

/*
    initBooking Query :

        Purpose :
            Reserves inventory during booking initialization
            before payment completion.

        Logic :
            1. Find available inventory.
            2. Increase reservedCount.
            3. Keep rooms unavailable to others.
            4. Await payment completion.

        Formula :

            Before :

                reservedCount = 2

            After Reservation :

                reservedCount = 4

        Query Details :

            SET i.reservedCount =
                i.reservedCount + :numberOfRooms

        Triggered By :

            initialiseBooking()
                    ↓
            initBooking()

        Booking Flow :

            Available Room
                    ↓
             initBooking()
                    ↓
                Reserved
                    ↓
          Payment Pending

        Result :
            Inventory becomes temporarily reserved.

        Note :
            - Executed before payment.
            - Reservation expires if payment is not completed.
            - Prevents overbooking during checkout.
*/