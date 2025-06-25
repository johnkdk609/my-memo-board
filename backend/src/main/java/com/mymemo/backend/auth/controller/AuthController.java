package com.mymemo.backend.auth.controller;

import com.mymemo.backend.auth.dto.*;
import com.mymemo.backend.auth.service.AuthService;
import com.mymemo.backend.auth.util.JwtUtil;
import com.mymemo.backend.entity.User;
import com.mymemo.backend.global.exception.CustomException;
import com.mymemo.backend.global.exception.ErrorCode;
import com.mymemo.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API", description = "인증 관련 API 문서입니다.")
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
    @Operation(summary = "회원가입", description = "이메일, 비밀번호 등 정보를 바탕으로 회원가입을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "409", description = "이메일 중복 등 충돌")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        authService.signup(requestDto);     // 회원 저장 로직 위임
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }

    /**
     * 로그인 API
     * @param request 이메일, 비밀번호
     * @return JWT accessToken + RefreshToken 응답
     */
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 통해 로그인하고, AccessToken과 RefreshToken을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 이메일/비밀번호 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request, activeProfile));
    }

    /**
     * Access Token 재발급 API
     * @param requestDto 사용자 이메일과 Refresh Token
     * @return 새로운 Access Token 및 Refresh Token
     */
    @Operation(summary = "토큰 재발급", description = "Refresh Token을 검증해 새로운 Access Token과 Refresh Token을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "Refresh Token 유효성 실패"),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음")
    })
    @PostMapping("/reissue")
    public TokenResponseDto reissue(@RequestBody TokenReissueRequestDto requestDto) {
        return authService.reissue(requestDto);
    }

    /**
     * 로그아웃 API
     * @param requestDto 사용자 이메일
     * @return 로그아웃 성공 메시지
     */
    @Operation(summary = "로그아웃", description = "Redis에 저장된 Refresh Token을 삭제하여 로그아웃 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "404", description = "저장된 Refresh Token 없음")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody TokenLogoutRequestDto requestDto) {
        authService.logout(requestDto.getEmail());
        return ResponseEntity.ok("로그아웃 성공");
    }
}
