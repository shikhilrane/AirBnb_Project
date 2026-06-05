package com.shikhilrane.project.airBnbApp.advices;

import com.shikhilrane.project.airBnbApp.exception.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<APIResponse<?>> handleAuthenticationException(AuthenticationException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<APIResponse<?>> handleJwtException(JwtException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ex.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
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

        Purpose : Handles exceptions globally across the application.
                  Provides consistent and centralized error responses.

        Responsibilities :
            - Handle business exceptions
            - Handle authentication failures
            - Handle authorization failures
            - Handle JWT errors
            - Handle validation failures
            - Handle unexpected server errors
            - Return standardized API responses

        Methods :

            handleResourceNotFound()
                - Handles ResourceNotFoundException
                - Returns HTTP 404 NOT FOUND

            handleInternalServerError()
                - Handles unexpected exceptions
                - Returns HTTP 500 INTERNAL SERVER ERROR

            handleAuthenticationException()
                - Handles authentication failures
                - Returns HTTP 401 UNAUTHORIZED

            handleJwtException()
                - Handles invalid or expired JWT tokens
                - Returns HTTP 401 UNAUTHORIZED

            handleAccessDeniedException()
                - Handles authorization failures
                - Returns HTTP 403 FORBIDDEN

            handleInputValidationErrors()
                - Handles validation failures
                - Returns HTTP 400 BAD REQUEST

            buildErrorResponseEntity()
                - Builds standardized error response

        Exception Flow :

            Exception Thrown
                    ↓
            GlobalExceptionHandler
                    ↓
              ApiError
                    ↓
             APIResponse
                    ↓
              JSON Response

        Handled Exceptions :

            ResourceNotFoundException
                    ↓
                404 NOT FOUND

            AuthenticationException
                    ↓
              401 UNAUTHORIZED

            JwtException
                    ↓
              401 UNAUTHORIZED

            AccessDeniedException
                    ↓
                403 FORBIDDEN

            MethodArgumentNotValidException
                    ↓
              400 BAD REQUEST

            Exception
                    ↓
          500 INTERNAL SERVER ERROR

        Authentication Failure Flow :

            Invalid Credentials
                    ↓
        AuthenticationException
                    ↓
            Error Response
                    ↓
              401 Response

        JWT Failure Flow :

            Invalid Token
                    ↓
            JwtException
                    ↓
            Error Response
                    ↓
              401 Response

        Authorization Failure Flow :

            Protected API
                    ↓
            Access Denied
                    ↓
        AccessDeniedException
                    ↓
              403 Response

        Validation Failure Flow :

            Invalid Request
                    ↓
        Validation Failure
                    ↓
      MethodArgumentNotValidException
                    ↓
              400 Response

        Example Responses :

            Resource Not Found :

                {
                    "error": {
                        "status": "404 NOT_FOUND",
                        "message": "Hotel not found with ID: 1"
                    }
                }

            Authentication Failure :

                {
                    "error": {
                        "status": "401 UNAUTHORIZED",
                        "message": "Bad credentials"
                    }
                }

            Authorization Failure :

                {
                    "error": {
                        "status": "403 FORBIDDEN",
                        "message": "Access Denied"
                    }
                }

            Validation Failure :

                {
                    "error": {
                        "status": "400 BAD_REQUEST",
                        "message": "Input validation failed",
                        "subErrors": [
                            "Hotel name is required"
                        ]
                    }
                }

        Benefits :
            - Centralized exception handling
            - Consistent API responses
            - Cleaner controllers and services
            - Easier debugging
            - Better client-side error handling
            - Improved maintainability

        Security Features :
            - Handles authentication failures
            - Handles authorization failures
            - Handles JWT validation errors
            - Prevents internal exception leakage

        Note :
            - Applies globally to all controllers.
            - Returns standardized APIResponse format.
            - Security exceptions are handled centrally.
            - Validation errors return all failed validations.

        This class acts as the global exception handling layer
        of the application.
*/