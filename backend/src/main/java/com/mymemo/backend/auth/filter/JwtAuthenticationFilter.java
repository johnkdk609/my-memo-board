package com.mymemo.backend.auth.filter;

import com.mymemo.backend.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j      // 운영 환경에서도 문제 추적 가능하도록 도입
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // 생성자를 통해 JwtUtil 주입
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        log.debug("JwtAuthenticationFilter - 요청 감지됨");

        String authHeader = request.getHeader("Authorization");     // Authorization 헤더 추출
        log.debug("Authorization 헤더: {}", authHeader);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);     // "Bearer " 이후 토큰만 추출
            if (jwtUtil.isTokenValid(token)) {      // 토큰 유효성 검사
                String email = jwtUtil.getEmailFromToken(token);    // 이메일(subject) 추출

                log.info("유효한 JWT, 사용자 이메일: {}", email);

                // 인증 객체 생성 (권한 정보는 현재 null)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );      // 요청 정보 세부 설정 (IP, 세션 등 포함됨)

                SecurityContextHolder.getContext().setAuthentication(authentication);   // 현재 요청의 SecurityContext에 인증 정보 저장

                log.debug("SecurityContext에 인증 정보 등록 완료");
            } else {
                log.warn("JWT 유효성 검사 실패");
            }
        } else {
            log.debug("Authorization 헤더 없음 또는 Bearer 형식 불일치: {}", authHeader);
        }

        filterChain.doFilter(request, response);    // 다음 필터로 요청 전달 (필수)
    }

}
