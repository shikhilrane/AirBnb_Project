package com.shikhilrane.project.airBnbApp.service;

import com.shikhilrane.project.airBnbApp.dto.ProfileUpdateRequestDto;
import com.shikhilrane.project.airBnbApp.dto.UserDto;
import com.shikhilrane.project.airBnbApp.entity.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getUserById(Long id);
    UserDetails loadUserByUsername(String username);

    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
