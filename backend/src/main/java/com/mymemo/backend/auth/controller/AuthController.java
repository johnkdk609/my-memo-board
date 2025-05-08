package com.mymemo.backend.auth.controller;

import com.mymemo.backend.auth.dto.LoginRequestDto;
import com.mymemo.backend.auth.dto.LoginResponseDto;
import com.mymemo.backend.auth.dto.SignupRequestDto;
import com.mymemo.backend.auth.service.AuthService;
import com.mymemo.backend.auth.util.JwtUtil;
import com.mymemo.backend.entity.User;
import com.mymemo.backend.global.exception.CustomException;
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
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);      // 이메일 존재 여부 확인

        // 유효하지 않은 경우 (이메일 없음 or 비밀번호 불일치)
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String message;

            // dev 프로필이면 상세 메시지 제공하고, prod는 통합된 보안 메시지 제공한다.
            if ("dev".equals(activeProfile)) {
                message = user == null ? "존재하지 않는 이메일 주소입니다." : "비밀번호가 일치하지 않습니다.";
            } else {
                message = "이메일 또는 비밀번호가 일치하지 않습니다.";
            }

            // 예외 발생 시 GlobalExceptionHandler가 JSON 형태로 응답 처리
            throw new CustomException(message, 400);
        }

        // 인증 성공 시 JWT 발급
        String accessToken = jwtUtil.createAccessToken(user.getEmail());

        // accessToken 담아 응답
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }
}
