package com.shikhilrane.project.airBnbApp.security.config;

import com.shikhilrane.project.airBnbApp.security.filter.JWTAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;          // JWT authentication filter

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;      // Handles security exceptions globally

    // Configures application security rules and JWT authentication
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){

        httpSecurity.
                csrf(csrfConfig -> csrfConfig.disable())              // Disables CSRF protection for stateless APIs
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)    // Uses stateless session management for JWT authentication
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)     // Executes JWT filter before UsernamePasswordAuthenticationFilter
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")        // Allows only ADMIN users to access admin APIs
                        .requestMatchers("/hotels/**", "/rooms/**", "/inventory/**").hasAnyRole("ADMIN", "HOTEL_MANAGER") // Allows hotel management operations
                        .requestMatchers("/bookings/**", "/users/**", "/guests/**", "/hotel-manager-requests/**").authenticated() // Allows authenticated users to access protected APIs
                        .anyRequest().permitAll()                                               // Allows all remaining APIs without authentication
                )
                .exceptionHandling(exHandlingConfig ->
                        exHandlingConfig.accessDeniedHandler(accessDeniedHandler())             // Handles AccessDeniedException globally
                );

        return httpSecurity.build();                                           // Builds Spring Security filter chain
    }

    // Creates BCrypt password encoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Creates AuthenticationManager bean used during login
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Handles authorization failures (403 Forbidden)
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);  // Delegates exception handling to GlobalExceptionHandler
        };
    }
}

/*
    WebSecurityConfig

        Purpose :
            Configures authentication, authorization,
            JWT security, and access control rules
            for the application.

        Responsibilities :
            - Configure Spring Security
            - Configure JWT authentication
            - Configure endpoint authorization
            - Configure password encryption
            - Configure stateless authentication
            - Handle authorization failures

        Security Components :

            JWTAuthFilter
                - Extracts JWT token
                - Validates JWT token
                - Authenticates user

            PasswordEncoder
                - Encrypts passwords using BCrypt

            AuthenticationManager
                - Handles login authentication

            AccessDeniedHandler
                - Handles authorization failures

        Authentication Flow :

            Login Request
                    ↓
            Email + Password
                    ↓
            AuthenticationManager
                    ↓
            User Authenticated
                    ↓
            JWT Generated
                    ↓
            API Requests

        Authorization Flow :

            Incoming Request
                    ↓
            JWTAuthFilter
                    ↓
            Validate Token
                    ↓
            Extract Roles
                    ↓
            Security Rules
                    ↓
            Access Granted / Denied

        Endpoint Access Rules :

            /admin/**
                ↓
                ADMIN Only

            /hotels/**
            /rooms/**
            /inventory/**
                ↓
                ADMIN
                    OR
                HOTEL_MANAGER

            /bookings/**
            /users/**
            /guests/**
            /hotel-manager-requests/**
                ↓
                Authenticated Users

            All Remaining APIs
                ↓
                Public Access

        Security Features :

            Stateless Authentication
                - No HTTP session storage

            JWT Authentication
                - Token-based security

            BCrypt Password Encoding
                - Secure password storage

            Role-Based Authorization
                - Endpoint-level access control

            Access Denied Handling
                - Centralized exception processing

        Business Use :
            - User authentication
            - User authorization
            - API protection
            - Hotel manager access control
            - Admin access control
            - Secure booking operations

        Note :
            - Uses JWT-based authentication.
            - Session creation is disabled.
            - Passwords are encrypted using BCrypt.
            - Security exceptions are delegated to GlobalExceptionHandler.
            - Authorization is role-based.

        This configuration acts as the central
        security layer of the application.
*/