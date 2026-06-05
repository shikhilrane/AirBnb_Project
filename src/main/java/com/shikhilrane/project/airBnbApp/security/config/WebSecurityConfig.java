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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){

        httpSecurity.
                csrf(csrfConfig -> csrfConfig.disable())              // Disables CSRF protection for stateless APIs
                .sessionManagement(sessionConfig ->
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)    // Uses stateless session management for JWT authentication
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)     // Executes JWT filter before UsernamePasswordAuthenticationFilter
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("HOTEL_MANAGER")        // Allows only HOTEL_MANAGER users to access admin APIs
                        .requestMatchers("/bookings/**").authenticated()              // Allows only authenticated users to access booking APIs
                        .anyRequest().permitAll()                                               // Allows all remaining APIs without authentication
                )
                .exceptionHandling(exHandlingConfig ->
                        exHandlingConfig.accessDeniedHandler(accessDeniedHandler())             // Handles AccessDeniedException globally
                );

        return httpSecurity.build();
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
