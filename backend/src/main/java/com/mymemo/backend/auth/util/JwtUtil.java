package com.mymemo.backend.auth.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;      // JWT 서명을 위한 암호화 키
    private final long expirationMs;    // JWT 만료 시간 (밀리초 단위, yml에서 주입 받음)

    // 생성자에서 secret 키와 만료시간을 yml로부터 주입받아 초기화
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());       // HS256에 맞는 Key 객체 생성
        this.expirationMs = expirationMs;
    }

    // 액세스 토큰 생성: 이메일(subject) 포함, 현재 시간 발급, 만료 시각 설정
    public String createAccessToken(String email) {
        Date now = new Date();  // 현재 시간 생성
        return Jwts.builder()
                .setSubject(email)                                          // JWT의 subject 필드에 email 저장
                .setIssuedAt(now)                                           // 발급 시간
                .setExpiration(new Date(now.getTime() + expirationMs))      // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)                    // HS256 알고리즘 + 키로 서명
                .compact();                                                 // JWT 문자열로 변환
    }

    // 토큰에서 이메일(subject)을 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)         // 서명 검증용 키 설정
                .build()
                .parseClaimsJws(token)      // 서명 검증 및 claim 파싱
                .getBody()
                .getSubject();              // 이메일 반환
    }

    // 토큰 유효성 검사 (형식 및 서명 체크)
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)         // 서명 검증용 키 설정
                    .build()
                    .parseClaimsJws(token);     // 예외 발생 없으면 유효함
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 서명이 틀리거나, 만료됐거나, 형식이 이상하면 false 반환
            return false;
        }
    }
}
