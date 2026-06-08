package com.shikhilrane.project.airBnbApp.service;

import com.shikhilrane.project.airBnbApp.dto.RoomDto;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface RoomService {
    RoomDto createNewRoom(Long hotelId, RoomDto roomDto);
    List<RoomDto> getAllRoomsInHotel(Long hotelId);
    RoomDto getRoomById(Long roomId);
    void deleteRoomById(Long roomId);
    RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto);
}
