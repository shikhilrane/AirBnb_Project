package com.shikhilrane.project.airBnbApp.entity;

import com.shikhilrane.project.airBnbApp.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                    // Unique identifier for each user

    @Column(unique = true, nullable = false)
    private String email;                               // Unique email used for login

    @Column(nullable = false)
    private String password;                            // Encrypted user password

    private String name;                                // Full name of the user

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;                            // Roles assigned to the user for authorization

}

/*
    User Entity

    Purpose : Represents a registered user of the Airbnb platform.
              Stores authentication details and roles used for
              authorization and access control.

    This entity stores :
        - Email address
        - Encrypted password
        - User name
        - User roles (USER, HOTEL_MANAGER, ADMIN, etc.)

    Relationships :
        - One User -> Many Bookings
        - One User -> Many Guests

    Example :
        User #1
            Name     : Rahul Sharma
            Email    : rahul@gmail.com
            Roles    : USER
        User #2
            Name     : Admin
            Email    : admin@airbnb.com
            Roles    : ADMIN

    Business Use :
        - Authenticates users during login.
        - Determines user permissions using roles.
        - Allows users to create bookings.
        - Allows users to manage saved guests.
        - Supports role-based access control throughout the system.

    Example Roles :

        USER
            - Search hotels
            - Create bookings
            - Manage guests

        HOTEL_MANAGER
            - Manage hotels
            - Manage rooms
            - Manage inventory

        ADMIN
            - Full system access

    Note :
        - Email must be unique across the platform.
        - Password should always be stored in encrypted form.
        - A user can have multiple roles.
        - Roles are stored in a separate collection table.

    Security Note :

        Never store plain text passwords.

        Example :

            Password  : myPassword123
            Stored As : $2a$10$xxxxxxxxxxxxxxxxxxxxx

        Use BCryptPasswordEncoder for password encryption.

    Each record in the "app_user" table represents one registered platform user.
*/