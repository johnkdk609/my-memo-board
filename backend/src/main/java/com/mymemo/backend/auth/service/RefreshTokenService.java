package com.mymemo.backend.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    // Refresh Token 저장 (key = email or userId, value = refreshToken)
    public void saveRefreshToken(String key, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(expirationMillis));
    }

    // Refresh Token 조회
    public String getRefreshToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Refresh Token 삭제 (로그아웃 시)
    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }

    // Refresh Token 존재 여부 확인
    public boolean hasValidRefreshToken(String key, String token) {
        String stored = getRefreshToken(key);
        return stored != null && stored.equals(token);
    }
}
