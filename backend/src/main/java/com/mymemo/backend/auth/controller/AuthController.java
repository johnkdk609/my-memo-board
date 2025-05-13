package com.mymemo.backend.auth.controller;

import com.mymemo.backend.auth.dto.LoginRequestDto;
import com.mymemo.backend.auth.dto.LoginResponseDto;
import com.mymemo.backend.auth.dto.SignupRequestDto;
import com.mymemo.backend.auth.service.AuthService;
import com.mymemo.backend.auth.util.JwtUtil;
import com.mymemo.backend.entity.User;
import com.mymemo.backend.global.exception.CustomException;
import com.mymemo.backend.global.exception.ErrorCode;
import com.mymemo.backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;              // 회원가입 로직 담당 서비스
    private final UserRepository userRepository;        // 이메일로 사용자 조회
    private final PasswordEncoder passwordEncoder;      // 비밀번호 검증용 (BCrypt)
    private final JwtUtil jwtUtil;                      // JWT 토큰 발급 유틸

    @Value("${spring.profiles.active}")                 // 현재 활성화된 프로필 (dev/prod)
    private String activeProfile;

    /**
     * 회원가입 API
     * @param requestDto 이메일, 비밀번호, 닉네임, 생일
     * @return 회원가입 성공 메시지
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        authService.signup(requestDto);     // 회원 저장 로직 위임
        return ResponseEntity.ok("회원가입 성공");
    }

    /**
     * 로그인 API
     * @param request 이메일, 비밀번호
     * @return JWT accessToken 응답
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        User user;

        if ("dev".equals(activeProfile)) {
            // 1. 이메일 존재 확인 -> 없으면 바로 예외
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

            // 2. 비밀번호 불일치 -> 예외
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
            }
        } else {
            // prod 환경: 통합 메시지용 처리
            user = userRepository.findByEmail(request.getEmail()).orElse(null);

            if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
            }
        }

        // 인증 성공 시 JWT 발급
        String accessToken = jwtUtil.createAccessToken(user.getEmail());

        // accessToken 담아 응답
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }
}
