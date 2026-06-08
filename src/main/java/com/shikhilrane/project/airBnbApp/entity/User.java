package com.shikhilrane.project.airBnbApp.entity;

import com.shikhilrane.project.airBnbApp.entity.enums.Gender;
import com.shikhilrane.project.airBnbApp.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "app_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                    // Unique identifier for each user

    @Column(unique = true, nullable = false)
    private String email;                               // Unique email used for login

    @Column(nullable = false)
    private String password;                            // Encrypted user password

    private String name;                                // Full name of the user

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;                              // User gender information

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;                            // Roles assigned to the user for authorization

    // Returns user roles as Spring Security authorities
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    // Returns email as username for authentication
    @Override
    public String getUsername() {
        return email;
    }

    // Compares two User objects for equality
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(email, user.email)
                && Objects.equals(password, user.password)
                && Objects.equals(name, user.name)
                && Objects.equals(roles, user.roles);
    }

    // Generates hash code for User object
    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, name, roles);
    }
}

/*
    User Entity

        Purpose : Represents a registered user of the Airbnb platform.
                  Stores login information and roles used for authentication and authorization.

        This entity stores :
            - User ID
            - Email address
            - Encrypted password
            - User name
            - User roles

        Relationships :
            - One User -> Many Hotels (as owner)
            - One User -> Many Bookings
            - One User -> Many Guests

        Example :

            User #1

                Name  : Rahul Sharma
                Email : rahul@gmail.com
                Role  : GUEST

            User #2

                Name  : Amit Patil
                Email : amit@gmail.com
                Role  : HOTEL_MANAGER

        Business Use :
            - User signup and login
            - JWT authentication
            - Role-based authorization
            - Hotel ownership validation
            - Booking management
            - Guest management

        Roles :

            GUEST :
                - Search hotels
                - Create bookings
                - Add guests

            HOTEL_MANAGER :
                - Create hotels
                - Update hotels
                - Delete hotels
                - Manage rooms
                - Manage inventory

        Security Features :

            UserDetails
                - Integrates with Spring Security

            getAuthorities()
                - Converts roles into Spring authorities

                Example :

                    HOTEL_MANAGER
                        ↓
                    ROLE_HOTEL_MANAGER

            getUsername()
                - Uses email as username

        Authentication Flow :

            Login Request
                    ↓
            Email + Password
                    ↓
            Spring Security
                    ↓
            User Loaded
                    ↓
            JWT Generated

        Authorization Flow :

            JWT Token
                    ↓
            User Extracted
                    ↓
            Roles Extracted
                    ↓
            Access Granted / Denied

        Example :

            User Role : HOTEL_MANAGER

            API : POST /admin/hotels

            Result : Access Granted

        Note :
            - Email must be unique.
            - Password must be encrypted.
            - Multiple roles are supported.
            - Roles are stored in a separate collection table.
            - Used by Spring Security authentication system.

        This entity acts as the central user and security model
        of the application.
*/