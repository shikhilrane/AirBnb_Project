package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.entity.Inventory;
import com.shikhilrane.project.airBnbApp.entity.Room;
import com.shikhilrane.project.airBnbApp.repository.InventoryRepository;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();                            // Gets current date
        LocalDate endDate = today.plusYears(1);            // Calculates date after one year
        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())                // Associates inventory with hotel
                    .room(room)                            // Associates inventory with room
                    .date(date)                            // Sets inventory date
                    .bookedCount(0)                        // Initially no rooms are booked
                    .totalCount(room.getTotalCount())      // Sets total available rooms
                    .price(room.getBasePrice())            // Sets base room price
                    .surgeFactor(BigDecimal.ONE)           // Default surge factor = 1.0
                    .city(room.getHotel().getCity())       // Stores city for faster search
                    .closed(false)                         // Keeps booking open by default
                    .build();
            inventoryRepository.save(inventory);
        }                                                  // This loop generates daily inventory records for a room for the next 1 year.
    }

    @Override
    public void deleteFutureInventories(Room room) {
        inventoryRepository.deleteByRoom(room);            // Deletes all inventory records associated with the room
    }
}
