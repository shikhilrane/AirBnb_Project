package com.shikhilrane.project.airBnbApp.entity;

import com.shikhilrane.project.airBnbApp.entity.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class HotelManagerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // Unique request identifier

    @ManyToOne
    private User user;                  // User requesting hotel manager access

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;       // Current approval status of the request
}

/*
    HotelManagerRequest

        Purpose :
            Represents a hotel manager access request
            submitted by a user.

        Responsibilities :
            - Store manager access requests
            - Track request ownership
            - Maintain approval status
            - Support role upgrade workflow

        Fields :

            id
                - Unique request identifier

            user
                - User who submitted the request

            status
                - Current state of the request
                - Examples :
                    • PENDING
                    • APPROVED
                    • REJECTED

        Entity Relationships :

            HotelManagerRequest
                    ↓
                Many-To-One
                    ↓
                  User

            One User
                    ↓
            Can Create Multiple Requests

        Request Lifecycle :

            Request Created
                    ↓
                PENDING
                    ↓
            Admin Review
                ↓     ↓
          APPROVED  REJECTED

        Database Role :
            - Stores hotel manager access requests
            - Maintains request audit information
            - Supports approval workflow persistence

        Business Use :
            - Hotel manager onboarding
            - Access approval workflow
            - User role upgrade process
            - Administrative review operations

        Note :
            - Status is stored as a String in the database.
            - Each request belongs to a user.
            - Approval processing is handled in the service layer.
            - Entity only represents persisted request data.

        This entity acts as the persistence model
        for hotel manager access requests.
*/