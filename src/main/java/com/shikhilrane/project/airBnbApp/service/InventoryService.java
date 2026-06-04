package com.shikhilrane.project.airBnbApp.service;

import com.shikhilrane.project.airBnbApp.dto.HotelPriceDto;
import com.shikhilrane.project.airBnbApp.dto.HotelSearchReuestDto;
import com.shikhilrane.project.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchReuestDto hotelSearchReuestDto);
}
