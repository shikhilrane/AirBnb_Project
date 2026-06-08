package com.shikhilrane.project.airBnbApp.controller;

import com.shikhilrane.project.airBnbApp.dto.HotelManagerRequestDto;
import com.shikhilrane.project.airBnbApp.service.HotelManagerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final HotelManagerRequestService service;

    // Retrieves all pending hotel manager requests
    @GetMapping("/hotel-manager-requests")
    public ResponseEntity<List<HotelManagerRequestDto>> getPendingRequests() {
        return ResponseEntity.ok(service.getPendingRequests());      // Returns pending approval requests
    }

    // Approves a hotel manager request
    @PatchMapping("/hotel-manager-requests/{requestId}/approve")
    public ResponseEntity<Void> approveRequest(@PathVariable Long requestId) {
        service.approveRequest(requestId);                           // Approves request and grants access
        return ResponseEntity.noContent().build();                  // Returns successful approval response
    }

    // Rejects a hotel manager request
    @PatchMapping("/hotel-manager-requests/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable Long requestId) {
        service.rejectRequest(requestId);                           // Rejects manager request
        return ResponseEntity.noContent().build();                  // Returns successful rejection response
    }
}

/*
    AdminController

        Purpose :
            Handles administrative operations of the application.

        Responsibilities :
            - View pending hotel manager requests
            - Approve hotel manager requests
            - Reject hotel manager requests

        Endpoints :

            GET /admin/hotel-manager-requests
                - Retrieves all pending requests

            PATCH /admin/hotel-manager-requests/{requestId}/approve
                - Approves a hotel manager request

            PATCH /admin/hotel-manager-requests/{requestId}/reject
                - Rejects a hotel manager request

        Hotel Manager Approval Flow :

            User Requests Hotel Manager Access
                            ↓
            Request Submitted
                            ↓
            Admin Reviews Request
                    ↓               ↓
                Approve          Reject
                    ↓               ↓
            Manager Access      Request Closed

        Business Use :
            - Role management
            - Access control
            - Hotel manager onboarding
            - Administrative approval workflow

        Security Features :
            - Admin-only operations
            - Controlled role assignment
            - Request validation before approval

        Note :
            - Only pending requests can be reviewed.
            - Approval grants hotel manager privileges.
            - Rejection denies hotel manager access.

        This controller acts as the administrative
        approval gateway for hotel manager requests.
*/