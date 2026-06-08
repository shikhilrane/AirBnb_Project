package com.shikhilrane.project.airBnbApp.service;

import com.shikhilrane.project.airBnbApp.dto.HotelManagerRequestDto;

import java.util.List;

public interface HotelManagerRequestService {

    HotelManagerRequestDto createRequest();

    List<HotelManagerRequestDto> getPendingRequests();

    void approveRequest(Long requestId);

    void rejectRequest(Long requestId);
}