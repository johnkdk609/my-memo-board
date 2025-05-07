package com.mymemo.backend.auth.util;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final String secret = "myjwtsecretmyjwtsecretmyjwtsecretmyjwtsecret";   // 최소 256bit
    private final long expirationMs = 1000 * 60 * 60;   // 1시간

    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());
}
