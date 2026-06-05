package com.shikhilrane.project.airBnbApp.security;

import com.shikhilrane.project.airBnbApp.dto.LoginDto;
import com.shikhilrane.project.airBnbApp.dto.SignUpRequestDto;
import com.shikhilrane.project.airBnbApp.dto.UserDto;

public interface AuthService {
    UserDto signUp(SignUpRequestDto signUpRequestDto);
    String[] login(LoginDto loginDto);
    String refreshToken(String refreshToken);
}
