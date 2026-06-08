package com.shikhilrane.project.airBnbApp.service;

import com.shikhilrane.project.airBnbApp.dto.HotelPriceDto;
import com.shikhilrane.project.airBnbApp.dto.HotelSearchReuestDto;
import com.shikhilrane.project.airBnbApp.dto.InventoryDto;
import com.shikhilrane.project.airBnbApp.dto.UpdateInventoryRequestDto;
import com.shikhilrane.project.airBnbApp.entity.Room;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchReuestDto hotelSearchReuestDto);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
