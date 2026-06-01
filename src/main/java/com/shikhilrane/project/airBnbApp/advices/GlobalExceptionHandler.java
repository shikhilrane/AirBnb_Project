package com.shikhilrane.project.airBnbApp.advices;

import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)      // Handles ResourceNotFoundException
    public ResponseEntity<APIResponse<?>> handleResourceNotFound(ResourceNotFoundException exception){

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)               // Sets HTTP status as 404
                .message(exception.getMessage())            // Uses exception message
                .build();

        return buildErrorResponseEntity(apiError);          // Returns standardized error response
    }


    @ExceptionHandler(Exception.class)                      // Handles all unhandled exceptions
    public ResponseEntity<APIResponse<?>> handleInternalServerError(Exception exception){

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)   // Sets HTTP status as 500
                .message(exception.getMessage())            // Uses exception message
                .build();

        return buildErrorResponseEntity(apiError);          // Returns standardized error response
    }


    @ExceptionHandler(MethodArgumentNotValidException.class) // Handles validation failures
    public ResponseEntity<APIResponse<?>> handleInputValidationErrors(MethodArgumentNotValidException e){

        List<String> errors = e
                .getBindingResult()                          // Gets validation result
                .getAllErrors()                              // Gets all validation errors
                .stream()
                .map(objectError -> objectError.getDefaultMessage()) // Extracts validation messages
                .collect(Collectors.toList());              // Converts messages into a list

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)             // Sets HTTP status as 400
                .message("Input validation failed")         // Generic validation error message
                .subErrors(errors)                          // Stores all validation error messages
                .build();

        return buildErrorResponseEntity(apiError);          // Returns standardized error response
    }

    private ResponseEntity<APIResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(
                new APIResponse<>(apiError),                // Wraps error inside APIResponse
                apiError.getStatus()                        // Uses status from ApiError
        );
    }
}

/*
    GlobalExceptionHandler

        Purpose : Handles exceptions globally for the entire application.
                  Provides consistent error responses for all APIs.

        Responsibilities :
            - Handle resource not found exceptions
            - Handle validation errors
            - Handle unexpected server errors
            - Return standardized API error responses

        Handled Exceptions :

            ResourceNotFoundException
                - Returned when requested data does not exist
                - Returns HTTP 404 NOT FOUND

            MethodArgumentNotValidException
                - Returned when validation fails
                - Returns HTTP 400 BAD REQUEST

            Exception
                - Catches all unhandled exceptions
                - Returns HTTP 500 INTERNAL SERVER ERROR

        Error Response Flow :

            Exception Thrown
                    ↓
            GlobalExceptionHandler
                    ↓
            ApiError Created
                    ↓
            APIResponse Wrapped
                    ↓
            JSON Response Returned

        Example :

            Hotel Not Found

                Request :
                    GET /admin/hotels/100

                Response :
                    {
                        "success": false,
                        "error": {
                            "status": "NOT_FOUND",
                            "message": "Hotel not found with ID: 100"
                        }
                    }

        Validation Error Example :

                Request :
                    POST /admin/hotels

                Invalid Input :
                    {
                        "name": ""
                    }

                Response :
                    {
                        "success": false,
                        "error": {
                            "status": "BAD_REQUEST",
                            "message": "Input validation failed",
                            "subErrors": [
                                "Hotel name is required",
                                "City is required"
                            ]
                        }
                    }

        Benefits :
            - Centralized exception handling
            - Cleaner controllers and services
            - Consistent API responses
            - Easier maintenance and debugging

        This class acts as the global error handling layer of the application.
*/