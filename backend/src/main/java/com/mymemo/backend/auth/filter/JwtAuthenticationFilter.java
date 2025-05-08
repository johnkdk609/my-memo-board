package com.mymemo.backend.auth.filter;

import com.mymemo.backend.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

        String authHeader = request.getHeader("Authorization");     // Authorization 헤더 추출

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);     // "Bearer " 이후 토큰만 추출
            if (jwtUtil.isTokenValid(token)) {      // 토큰 유효성 검사
                String email = jwtUtil.getEmailFromToken(token);    // 이메일(subject) 추출

                // 인증 객체 생성 (권한 정보는 현재 null)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, null);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));      // 요청 정보 세부 설정 (IP, 세션 등 포함됨)

                SecurityContextHolder.getContext().setAuthentication(authentication);   // 현재 요청의 SecurityContext에 인증 정보 저장
            }
        }

        filterChain.doFilter(request, response);    // 다음 필터로 요청 전달 (필수)
    }

}
