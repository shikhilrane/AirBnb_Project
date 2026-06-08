package com.shikhilrane.project.airBnbApp.service.impl;

import com.shikhilrane.project.airBnbApp.dto.HotelManagerRequestDto;
import com.shikhilrane.project.airBnbApp.entity.HotelManagerRequest;
import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.entity.enums.RequestStatus;
import com.shikhilrane.project.airBnbApp.entity.enums.Role;
import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import com.shikhilrane.project.airBnbApp.repository.HotelManagerRequestRepository;
import com.shikhilrane.project.airBnbApp.repository.UserRepository;
import com.shikhilrane.project.airBnbApp.service.HotelManagerRequestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.shikhilrane.project.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class HotelManagerRequestServiceImpl implements HotelManagerRequestService {

    private final HotelManagerRequestRepository hotelManagerRequestRepository;   // Performs hotel manager request database operations
    private final UserRepository userRepository;                                 // Performs user database operations
    private final ModelMapper modelMapper;                                       // Converts entities into DTOs

    // Creates a new hotel manager access request
    @Override
    @Transactional
    public HotelManagerRequestDto createRequest() {

        User user = getCurrentUser();                                            // Retrieves currently authenticated user

        if (user.getRoles().contains(Role.HOTEL_MANAGER)) {
            throw new IllegalStateException("User is already a HOTEL_MANAGER");
        }

        hotelManagerRequestRepository
                .findByUserAndStatus(user, RequestStatus.PENDING)                // Checks for existing pending request
                .ifPresent(request -> {
                    throw new IllegalStateException("Pending request already exists");
                });

        HotelManagerRequest request = new HotelManagerRequest();

        request.setUser(user);                                                   // Associates request with current user
        request.setStatus(RequestStatus.PENDING);                                // Marks request as pending

        request = hotelManagerRequestRepository.save(request);                   // Saves request

        return mapToDto(request);                                                // Converts entity into DTO
    }

    // Converts HotelManagerRequest entity into DTO
    private HotelManagerRequestDto mapToDto(HotelManagerRequest request) {

        HotelManagerRequestDto dto = modelMapper.map(request, HotelManagerRequestDto.class);

        dto.setUserId(request.getUser().getId());                                // Sets requester identifier
        dto.setUserEmail(request.getUser().getEmail());                          // Sets requester email
        dto.setUserName(request.getUser().getName());                            // Sets requester name

        return dto;
    }

    // Retrieves all pending hotel manager requests
    @Override
    public List<HotelManagerRequestDto> getPendingRequests() {

        return hotelManagerRequestRepository
                .findByStatus(RequestStatus.PENDING)                             // Retrieves pending requests
                .stream()
                .map(this::mapToDto)                                             // Converts entities into DTOs
                .toList();
    }

    // Approves a hotel manager request and grants HOTEL_MANAGER role
    @Override
    @Transactional
    public void approveRequest(Long requestId) {

        HotelManagerRequest request = hotelManagerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId)); // Fetches request or throws exception

        User user = request.getUser();                                           // Retrieves request owner

        user.getRoles().add(Role.HOTEL_MANAGER);                                 // Grants HOTEL_MANAGER role

        userRepository.save(user);                                               // Persists updated user roles

        request.setStatus(RequestStatus.APPROVED);                               // Marks request as approved

        hotelManagerRequestRepository.save(request);                             // Saves updated request
    }

    // Rejects a hotel manager request
    @Override
    @Transactional
    public void rejectRequest(Long requestId) {

        HotelManagerRequest request = hotelManagerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId)); // Fetches request or throws exception

        request.setStatus(RequestStatus.REJECTED);                               // Marks request as rejected

        hotelManagerRequestRepository.save(request);                             // Saves updated request
    }
}

/*
    HotelManagerRequestServiceImpl

        Purpose :
            Handles hotel manager access request workflow.
            Manages request creation, approval, and rejection.

        Responsibilities :
            - Create hotel manager requests
            - Prevent duplicate pending requests
            - Retrieve pending requests
            - Approve requests
            - Reject requests
            - Grant HOTEL_MANAGER role
            - Convert entities into DTOs

        Methods :

            createRequest()
                - Validates user role
                - Prevents duplicate requests
                - Creates new request
                - Marks request as PENDING

            mapToDto()
                - Converts entity into DTO
                - Populates user information

            getPendingRequests()
                - Retrieves pending requests
                - Converts requests into DTOs

            approveRequest()
                - Retrieves request
                - Grants HOTEL_MANAGER role
                - Updates request status

            rejectRequest()
                - Retrieves request
                - Updates request status

        Request Lifecycle :

            Request Created
                    ↓
                PENDING
                    ↓
            Admin Review
                ↓     ↓
          APPROVED  REJECTED

        Approval Flow :

            Pending Request
                    ↓
            Admin Approval
                    ↓
          Add HOTEL_MANAGER Role
                    ↓
            Save User
                    ↓
            APPROVED

        Rejection Flow :

            Pending Request
                    ↓
            Admin Rejection
                    ↓
                REJECTED

        Request Creation Flow :

            Authenticated User
                    ↓
            Role Validation
                    ↓
        Existing Request Check
                    ↓
            Create Request
                    ↓
                PENDING

        Business Use :
            - Hotel manager onboarding
            - Access approval workflow
            - Role upgrade process
            - Administrative review process

        Security Features :
            - Role validation
            - Duplicate request prevention
            - Controlled role assignment
            - Transaction-safe approval process

        Note :
            - HOTEL_MANAGER users cannot create new requests.
            - Only one pending request is allowed per user.
            - Approval grants HOTEL_MANAGER role.
            - Rejection does not modify user roles.
            - Business logic is executed inside transactions.

        This service acts as the central
        hotel manager approval workflow
        component of the application.
*/