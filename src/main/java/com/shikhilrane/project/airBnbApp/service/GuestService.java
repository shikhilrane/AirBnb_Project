package com.shikhilrane.project.airBnbApp.service;

import com.shikhilrane.project.airBnbApp.dto.GuestDto;

import java.util.List;
import java.util.Map;

public interface GuestService {
    GuestDto createNewGuest(GuestDto guestDto);

    List<GuestDto> getAllGuests();

    GuestDto getGuestById(Long guestId);

    GuestDto updateGuestById(Long guestId, GuestDto guestDto);

    GuestDto partiallyUpdateGuestById(Long guestId, Map<String, Object> updates);

    void deleteGuestById(Long guestId);
}
