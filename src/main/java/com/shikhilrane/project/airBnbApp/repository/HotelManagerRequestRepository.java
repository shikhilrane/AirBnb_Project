package com.shikhilrane.project.airBnbApp.repository;

import com.shikhilrane.project.airBnbApp.entity.HotelManagerRequest;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelManagerRequestRepository extends JpaRepository<HotelManagerRequest, Long> {
    Optional<HotelManagerRequest> findByUserAndStatus(User user, RequestStatus status);    // Finds a user's request by status
    List<HotelManagerRequest> findByStatus(RequestStatus status);                           // Retrieves all requests with a specific status
}

/*
    HotelManagerRequestRepository

        Purpose :
            Provides database access operations
            for HotelManagerRequest entities.

        Responsibilities :
            - Persist manager requests
            - Retrieve manager requests
            - Query requests by user
            - Query requests by status
            - Support approval workflows

        Methods :

            findByUserAndStatus()
                - Finds a user's request with a specific status

            findByStatus()
                - Retrieves all requests matching a status

        Request Submission Flow :

            User
                ↓
            Submit Request
                ↓
            Request Saved
                ↓
            HotelManagerRequestRepository
                ↓
            Database

        Duplicate Request Validation Flow :

            User
                ↓
            Create Request
                ↓
            findByUserAndStatus()
                ↓
            Existing Request Check
                ↓
            Allow / Reject Creation

        Admin Review Flow :

            Admin
                ↓
            View Pending Requests
                ↓
            findByStatus(PENDING)
                ↓
            Pending Requests Returned
                ↓
            Approve / Reject

        Request Lifecycle :

            Request Created
                    ↓
                PENDING
                    ↓
            Admin Review
                ↓     ↓
          APPROVED  REJECTED

        Business Use :
            - Hotel manager onboarding
            - Approval workflow management
            - Request validation
            - Administrative review operations
            - Role upgrade processing

        Note :
            - Extends JpaRepository for standard CRUD operations.
            - Uses Spring Data JPA derived query methods.
            - Supports status-based request filtering.
            - Supports duplicate request prevention checks.
            - Frequently used during approval workflows.

        This repository acts as the primary
        data access layer for hotel manager
        request persistence and approval queries.
*/