package com.shikhilrane.project.airBnbApp.advices;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private HttpStatus status;      // HTTP status code associated with the error
    private String message;         // Main error message describing the problem
    private List<String> subErrors; // Detailed validation or field-level error messages
}

/*
    ApiError

        Purpose : Represents error information returned to the client when an API request fails.

        This class stores :
            - HTTP status code
            - Error message
            - Detailed validation errors

        Fields :

            status
                - HTTP error status
                - Example : 404 NOT_FOUND

            message
                - Main error description
                - Example : Hotel not found with ID: 1

            subErrors
                - Additional error details
                - Usually used for validation failures

        Example :

            Resource Not Found

                {
                    "status": "NOT_FOUND",
                    "message": "Hotel not found with ID: 1"
                }

        Validation Error Example :

                {
                    "status": "BAD_REQUEST",
                    "message": "Input validation failed",
                    "subErrors": [
                        "Hotel name is required",
                        "City is required",
                        "Contact information is required"
                    ]
                }

        Business Use :
            - Standardizes error responses
            - Provides meaningful error messages
            - Helps clients understand request failures
            - Supports validation error reporting

        Flow :

            Exception Thrown
                    ↓
            GlobalExceptionHandler
                    ↓
            ApiError Created
                    ↓
            APIResponse Wrapper
                    ↓
            Client

        Note :
            - message contains the primary error.
            - subErrors contains detailed validation errors.
            - status represents the HTTP response status.

        This class acts as the standard error response model
        for the entire application.
*/