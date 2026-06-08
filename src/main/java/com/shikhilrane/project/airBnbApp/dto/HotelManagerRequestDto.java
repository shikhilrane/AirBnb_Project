package com.shikhilrane.project.airBnbApp.dto;

import com.shikhilrane.project.airBnbApp.entity.enums.RequestStatus;
import lombok.Data;

@Data
public class HotelManagerRequestDto {
    private Long id;                    // Unique request identifier
    private Long userId;                // Identifier of the requesting user
    private String userEmail;           // Email address of the requesting user
    private String userName;            // Name of the requesting user
    private RequestStatus status;       // Current approval status of the request
}

/*
    HotelManagerRequestDto

        Purpose :
            Transfers hotel manager request information
            between application layers.

        Responsibilities :
            - Represent hotel manager requests
            - Carry requester information
            - Carry request approval status
            - Support request management workflows

        Fields :

            id
                - Unique request identifier

            userId
                - User who submitted the request

            userEmail
                - Requester's email address

            userName
                - Requester's display name

            status
                - Current request status
                - Examples :
                    • PENDING
                    • APPROVED
                    • REJECTED

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
            - Access approval workflow
            - Administrative request review
            - User role upgrade process

        Note :
            - Used as a response DTO.
            - Contains request and requester information.
            - Status determines the current stage of approval.
            - Business logic is handled outside the DTO.

        This DTO acts as the data representation
        of a hotel manager access request.
*/