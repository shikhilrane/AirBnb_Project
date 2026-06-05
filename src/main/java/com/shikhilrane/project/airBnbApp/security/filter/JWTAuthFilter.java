package com.shikhilrane.project.airBnbApp.security.filter;

import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.security.JwtService;
import com.shikhilrane.project.airBnbApp.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;    // Handles JWT validation and user extraction
    private final UserService userService;  // Fetches user details from database

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;                    // Handles exceptions thrown inside filter

    // Validates JWT token and authenticates user for every request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");        // Get Authorization header from the incoming request
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {  // Check if header is missing or does not start with "Bearer "
                filterChain.doFilter(request, response);                                    // Skip JWT check and pass request to the next filter
                return;
            }

            String token = requestTokenHeader.substring(7).trim();              // Remove "Bearer " from header and extract the actual JWT token
            Long userIdFromToken = jwtService.getUserIdFromToken(token);                   // Extract userId from the JWT token using JwtService

            if (userIdFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null) { // Check if userId exists and no user is set in SecurityContext yet
                User userById = userService.getUserById(userIdFromToken);         // Fetch user details from database using the extracted userId
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userById, null, userById.getAuthorities()); // Create authentication object for this user
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)         // Attach request details like IP and session info
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Set authenticated user in Spring Security context
            }
            filterChain.doFilter(request, response);
        } catch (Exception e){
            handlerExceptionResolver.resolveException(request,response,null,e);            // Delegates exception handling to GlobalExceptionHandler
        }
    }
}

/*
    JWTAuthFilter

        Purpose : Authenticates users using JWT tokens.

        Responsibilities :
            - Read JWT token from request
            - Validate JWT token
            - Extract user information
            - Authenticate user
            - Populate Security Context

        Methods :

            doFilterInternal()
                - Reads Authorization header
                - Validates JWT token
                - Loads user from database
                - Authenticates user
                - Stores authentication in Security Context

        Authentication Flow :

            Client Request
                    ↓
            Authorization Header
                    ↓
            JWT Token
                    ↓
            User ID Extraction
                    ↓
            User Loaded
                    ↓
            Authentication Created
                    ↓
            Security Context Updated
                    ↓
            Request Continues

        Business Use :
            - Protects secured APIs
            - Supports stateless authentication
            - Integrates JWT with Spring Security
            - Authenticates users for every request

        Note :
            - Runs once per request.
            - Invalid JWT results in exception.
            - Security Context is populated only after successful authentication.

        This filter acts as the JWT authentication layer
        of the application.
*/