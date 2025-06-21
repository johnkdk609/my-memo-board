package com.mymemo.backend.auth.service;

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
}
