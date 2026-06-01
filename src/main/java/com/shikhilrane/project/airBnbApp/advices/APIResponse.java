package com.shikhilrane.project.airBnbApp.advices;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data                                           // Generates getters, setters, toString, equals, and hashCode methods
@JsonInclude(JsonInclude.Include.NON_NULL)      // Excludes null fields from JSON response
public class APIResponse<T> {

    @JsonFormat(pattern = "HH:mm:ss dd-MM-yyyy") // Formats timestamp in a readable date-time format
    private LocalDateTime timestamp;            // Time when the API response was generated
    private T data;                             // Stores successful response data
    private ApiError error;                     // Stores error details if request fails

    public APIResponse() {
        this.timestamp = LocalDateTime.now();   // Automatically sets current timestamp
    }

    public APIResponse(T data) {
        this();                                 // Calls default constructor to set timestamp
        this.data = data;                       // Stores successful response payload
    }

    public APIResponse(ApiError error) {
        this();                                 // Calls default constructor to set timestamp
        this.error = error;                     // Stores error information
    }
}

/*
    APIResponse

        Purpose : Provides a standard response format for all API responses in the application.

        This class stores :
            - Response timestamp
            - Success response data
            - Error details

        Response Types :

            Success Response
                - Contains data
                - Error is null

            Error Response
                - Contains error information
                - Data is null

        Example Success Response :

            {
                "timestamp": "15:30:20 01-06-2026",
                "data": {
                    "id": 1,
                    "name": "Hotel Lotus"
                }
            }

        Example Error Response :

            {
                "timestamp": "15:30:20 01-06-2026",
                "error": {
                    "status": "NOT_FOUND",
                    "message": "Hotel not found"
                }
            }

        Benefits :
            - Consistent API response structure
            - Easier frontend integration
            - Centralized success and error handling
            - Includes response generation time

        Flow :

            Controller
                 ↓
            APIResponse
                 ↓
            JSON Response
                 ↓
            Client

        Note :
            - Only non-null fields are included in the response.
            - Timestamp is automatically generated.
            - Data and Error are mutually exclusive.

        This class acts as the standard API response wrapper
        for the entire application.
*/