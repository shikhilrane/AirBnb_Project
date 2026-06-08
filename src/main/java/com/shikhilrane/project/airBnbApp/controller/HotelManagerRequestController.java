package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.HotelManagerRequestDto;
import com.shikhilrane.project.airBnbApp.service.HotelManagerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hotel-manager-requests")
@RequiredArgsConstructor
public class HotelManagerRequestController {

    private final HotelManagerRequestService service;

    // Creates a new hotel manager access request
    @PostMapping
    public ResponseEntity<HotelManagerRequestDto> createRequest() {
        return ResponseEntity.ok(service.createRequest());      // Submits request for admin approval
    }
}

/*
    HotelManagerRequestController

        Purpose :
            Handles hotel manager access requests submitted by users.

        Responsibilities :
            - Create hotel manager requests
            - Initiate manager approval workflow

        Endpoints :

            POST /hotel-manager-requests
                - Creates a new hotel manager request

        Request Flow :

            User
                ↓
            Submit Request
                ↓
            Request Created
                ↓
            Pending Approval
                ↓
            Admin Review

        Approval Flow :

            Request Submitted
                    ↓
                PENDING
                    ↓
            Admin Decision
                ↓
         Approve / Reject

        Business Use :
            - Hotel manager onboarding
            - Role upgrade requests
            - Access approval workflow
            - Administrative review process

        Security Features :
            - Authenticated user requests
            - Controlled role assignment
            - Approval-based privilege escalation

        Note :
            - Request creation does not grant manager access.
            - Admin approval is required before activation.
            - Business logic is delegated to the service layer.

        This controller acts as the entry point
        for hotel manager access requests.
*/