package com.shikhilrane.project.airBnbApp.service;

import com.shikhilrane.project.airBnbApp.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getUserById(Long id);
    UserDetails loadUserByUsername(String username);
}
