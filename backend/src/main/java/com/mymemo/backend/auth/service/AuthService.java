package com.mymemo.backend.auth.service;

import com.mymemo.backend.auth.dto.LoginRequestDto;
import com.mymemo.backend.auth.dto.SignupRequestDto;
import com.mymemo.backend.auth.dto.TokenReissueRequestDto;
import com.mymemo.backend.auth.dto.TokenResponseDto;
import com.mymemo.backend.auth.util.JwtUtil;
import com.mymemo.backend.entity.User;
import com.mymemo.backend.global.exception.CustomException;
import com.mymemo.backend.global.exception.ErrorCode;
import com.mymemo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    public void signup(SignupRequestDto dto) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 2. 비밀번호 확인 일치 여부
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. User 엔터티로 변환하여 저장
        User user = new User(
                encodedPassword,
                dto.getNickname(),
                dto.getEmail(),
                dto.getBirthDate()
        );

        userRepository.save(user);
    }

    public TokenResponseDto reissue(TokenReissueRequestDto dto) {
        String email = dto.getEmail();
        String requestRefreshToken = dto.getRefreshToken();

        // 1. 이메일로 사용자 존재 여부 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        // 2. Redis에서 해당 이메일로 저장된 Refresh Token 조회
        String storedToken = redisTemplate.opsForValue().get("RT:" + email);
        if (storedToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 3. 요청된 Refresh Token이 저장된 토큰과 일치하는지 확인
        if (!storedToken.equals(requestRefreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 4. 요청된 Refresh Token의 유효성 검증 (만료 여부, 시그니처 등)
        if (!jwtUtil.isTokenValid(requestRefreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 5. 유효한 경우 새로운 Access Token과 Refresh Token 발급
        String newAccessToken = jwtUtil.createAccessToken(email);
        String newRefreshToken = jwtUtil.createRefreshToken(email);

        // 6. Redis에 새로운 Refresh Token을 저장 (기존 것 대체), 유효기간 설정
        redisTemplate.opsForValue().set(
                "RT:" + email,
                newRefreshToken,
                jwtUtil.getRefreshTokenValidityInMs(),
                TimeUnit.MILLISECONDS
        );

        // 7. 새로운 토큰 쌍을 응답으로 반환
        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }

    public TokenResponseDto login(LoginRequestDto dto, String activeProfile) {
        User user;

        if ("dev".equals(activeProfile)) {      // dev 환경에서는 상세한 에러 메시지 제공
            user = userRepository.findByEmail(dto.getEmail())   // 이메일로 사용자 조회 (없으면 EMAIL_NOT_FOUND 에러)
                    .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {      // 비밀번호 검증 (틀리면 PASSWORD_MISMATCH 에러)
                throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
            }
        } else {
            user = userRepository.findByEmail(dto.getEmail()).orElse(null);     // prod 환경에서는 통합된 에러 메시지 제공 (보안 이유)

            if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {      // 이메일 없거나 비밀번호 틀리면 INVALID_CREDENTIALS 에러
                throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
            }
        }

        String accessToken = jwtUtil.createAccessToken(user.getEmail());    // 인증 성공 시 Access Token 생성
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail());  // Refresh Token 생성

        // Refresh Token을 Redis에 저장 (키: RT:이메일, TTL: 14일)
        redisTemplate.opsForValue().set(
                "RT:" + user.getEmail(),            // Redis 키
                refreshToken,                           // 저장할 값
                jwtUtil.getRefreshTokenValidityInMs(),  // 만료 시간(ms)
                TimeUnit.MILLISECONDS                   // 시간 단위
        );

        // Access Token + Refresh Token 응답 객체로 반환
        return new TokenResponseDto(accessToken, refreshToken);
    }

    public void logout(String email) {
        // Redis에서 Refresh Token 제거
        Boolean result = redisTemplate.delete("RT:" + email);

        if (result == null || !result) {
            // Redis에 해당 키가 없거나 삭제 실패
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }
}
