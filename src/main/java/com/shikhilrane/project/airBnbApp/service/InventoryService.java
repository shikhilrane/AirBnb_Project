package com.shikhilrane.project.airBnbApp.service;

import com.shikhilrane.project.airBnbApp.entity.Room;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room room);
}
