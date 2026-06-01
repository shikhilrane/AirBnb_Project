package com.shikhilrane.project.airBnbApp.repository;

import com.shikhilrane.project.airBnbApp.entity.Inventory;
import com.shikhilrane.project.airBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);       // Deletes all inventory records associated with a room
}
