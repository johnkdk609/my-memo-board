package com.mymemo.backend.auth.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;    // JWT 서명을 위한 암호화 키
    private final long expirationMs;    // JWT 만료 시간 (밀리초 단위, yml에서 주입 받음)

    // 생성자에서 secret 키와 만료시간을 yml로부터 주입받아 초기화
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-in-ms}") long expirationMs
    ) {
        // 시크릿 키 최소 길이 검증 (HMAC-SHA256은 256비트 이상 권장)
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT Secret key must be at least 256 bits (32 characters) long.");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes());       // HS256에 맞는 Key 객체 생성
        this.expirationMs = expirationMs;
    }

    // 액세스 토큰 생성: 이메일(subject) 포함, 현재 시간 발급, 만료 시각 설정
    public String createAccessToken(String email) {
        Date now = new Date();  // 현재 시간 생성
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")                    // typ 명시 (선택적이지만 권장됨)
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
            log.warn("JWT 유효성 검사 실패: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    @Value("${jwt.refresh-token-validity-in-ms}")
    private long refreshTokenValidityInMs;      // Refresh Token의 유효 기간 (application.yml에서 주입)

    // Refresh Token 생성 메서드
    public String createRefreshToken(String email) {
        Date now = new Date();      // 현재 시각
        Date expiry = new Date(now.getTime() + refreshTokenValidityInMs);       // 만료 시각 계산

        // JWT 빌더를 사용해 Refresh Token 생성
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")       // 토큰 타입 명시
                .setSubject(email)                             // 사용자 식별 정보 (이메일)
                .setIssuedAt(now)                              // 토큰 발급 시간
                .setExpiration(expiry)                         // 토큰 만료 시간 (14일)
                .signWith(key, SignatureAlgorithm.HS256)       // HMAC-SHA256 알고리즘과 비밀 키로 서명
                .compact();                                    // JWT 문자열로 압축 반환
    }

    // Redis에 저장한 TTL(만료 시간)을 위해 외부에서 접근 가능하게 제공
    public long getRefreshTokenValidityInMs() {
        return refreshTokenValidityInMs;    // application.yml에서 설정한 값 그대로 반환
    }

    // Access Token 남은 유효시간 반환 메서드
    public long getTokenRemainingTime(String token) {
        // 1. JWT 파서 객체를 생성하고 서명 키를 설정함
        //    -> 이 키를 이용해 토큰의 서명 유효성을 검증하면서 파싱 수행
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)                    // JWT 검증을 위한 서명 키 설정
                .build()
                .parseClaimsJws(token)                 // 토큰 파싱 및 서명 검증 (Claims 추출)
                .getBody()
                .getExpiration();                      // Claims에서 만료 시간(Date) 추출

        // 2. 만료 시각과 현재 시간의 차이를 계산 -> 남은 시간 (ms)
        return expiration.getTime() - System.currentTimeMillis();
    }
}
