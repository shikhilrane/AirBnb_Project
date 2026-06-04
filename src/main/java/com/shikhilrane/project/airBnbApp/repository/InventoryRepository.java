package com.shikhilrane.project.airBnbApp.repository;

import com.shikhilrane.project.airBnbApp.entity.Hotel;
import com.shikhilrane.project.airBnbApp.entity.Inventory;
import com.shikhilrane.project.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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
}

/*
    InventoryRepository

        Purpose :
            Provides database operations
            for Inventory records.

        Responsibilities :
            - Save inventory records
            - Delete inventory records
            - Lock inventory during booking
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

        Business Use :
            - Manages room availability.
            - Supports hotel search.
            - Supports booking flow.
            - Prevents overbooking.
            - Supports dynamic pricing updates.

        This repository acts as the data access layer
        for inventory management.
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