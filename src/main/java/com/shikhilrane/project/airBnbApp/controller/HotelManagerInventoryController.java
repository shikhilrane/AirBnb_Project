package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.InventoryDto;
import com.shikhilrane.project.airBnbApp.dto.UpdateInventoryRequestDto;
import com.shikhilrane.project.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class HotelManagerInventoryController {

    private final InventoryService inventoryService;       // Handles room inventory operations

    // Retrieves inventory records for a room
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));      // Returns inventory availability details
    }

    // Updates inventory availability for a room
    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId, @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto) {
        inventoryService.updateInventory(roomId, updateInventoryRequestDto);           // Updates room inventory records
        return ResponseEntity.noContent().build();                                     // Returns successful update response
    }
}

/*
    HotelManagerInventoryController

        Purpose :
            Handles inventory management operations for hotel rooms.

        Responsibilities :
            - Retrieve room inventory
            - Update room inventory availability
            - Manage room stock allocation

        Endpoints :

            GET /inventory/rooms/{roomId}
                - Retrieves inventory details for a room

            PATCH /inventory/rooms/{roomId}
                - Updates room inventory

        Inventory Management Flow :

            Hotel Manager
                    ↓
            Select Room
                    ↓
            View Inventory
                    ↓
            Update Availability
                    ↓
            Inventory Service
                    ↓
            Database Updated

        Inventory Update Flow :

            Inventory Update Request
                        ↓
            Validate Room
                        ↓
            Update Inventory Records
                        ↓
            Persist Changes
                        ↓
            Success Response

        Business Use :
            - Room availability management
            - Inventory planning
            - Reservation control
            - Occupancy management
            - Capacity tracking

        Security Features :
            - Role-based inventory access
            - Room ownership validation
            - Controlled inventory modification

        Note :
            - Inventory is managed at room level.
            - Updates affect future booking availability.
            - Inventory changes are handled by the service layer.
            - Controller only processes HTTP requests and responses.

        This controller acts as the inventory
        management entry point for hotel managers.
*/