package com.shikhilrane.project.airBnbApp.security;

import com.shikhilrane.project.airBnbApp.entity.User;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    Long getUserIdFromToken(String token);
}
