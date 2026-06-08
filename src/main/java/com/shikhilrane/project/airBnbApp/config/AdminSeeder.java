package com.shikhilrane.project.airBnbApp.config;

import com.shikhilrane.project.airBnbApp.entity.User;
import com.shikhilrane.project.airBnbApp.entity.enums.Role;
import com.shikhilrane.project.airBnbApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Creates default admin account during application startup
    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@gmail.com").isPresent()) {
            return;                                                             // Prevents duplicate admin creation
        }

        User admin = new User();                                                // Creates default admin user
        admin.setName("System Admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin123"));      // Stores encrypted password

        admin.setRoles(Set.of(Role.ADMIN));                                     // Assigns ADMIN role

        userRepository.save(admin);                                             // Persists admin user in database
    }
}

/*
    AdminSeeder

        Purpose :
            Creates a default administrator account when the application starts.

        Responsibilities :
            - Check if admin account already exists
            - Create default admin user
            - Encrypt admin password
            - Assign ADMIN role
            - Save admin user into database

        Startup Flow :

            Application Starts
                    ↓
            AdminSeeder Executes
                    ↓
            Check Admin Exists
                    ↓
            Yes ----------------→ Skip Creation
                    ↓ No
            Create Admin User
                    ↓
            Encrypt Password
                    ↓
            Assign ADMIN Role
                    ↓
            Save To Database

        Default Admin Details :

            Email :
                admin@gmail.com

            Password :
                admin123

            Role :
                ADMIN

        Business Use :
            - Initial system setup
            - Administrative access
            - System management
            - User and role administration

        Security Features :
            - Password stored in encrypted form
            - Role-based access control
            - Prevents duplicate admin creation

        Note :
            - Executes automatically during application startup.
            - Admin account is created only once.
            - Existing admin account will not be overwritten.

        This component ensures that the application
        always has an administrator account available.
*/