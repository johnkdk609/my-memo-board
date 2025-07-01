package com.mymemo.backend.global.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 인증 정보에서 현재 로그인한 사용자의 이메일(username)을 반환하는 유틸 클래스.
 *
 * Spring Security의 Authentication 객체에서 getName()을 호출하여 username을 가져오며,
 * 일반적으로 이 username은 사용자의 이메일 주소이다.
 */
public class SecurityUtil {

    /**
     * 현재 SecurityContext에 저장된 인증 정보에서 사용자 이메일을 반환한다.
     *
     * @return 로그인한 사용자의 이메일 (Authentication.getName())
     */
    public static String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * 현재 HTTP 요청의 Authorization 헤더에서 Access Token 값을 추출한다.
     *
     * Authorization 헤더는 일반적으로 다음과 같은 형태로 전달된다:
     *
     *     Authorization: Bearer {AccessToken}
     *
     * 이 메서드는 해당 헤더에서 "Bearer " 접두사를 제거한 실제 토큰 문자열만 반환한다.
     *
     * @return 요청 헤더에 포함된 Access Token (접두사 제거된 형태), 없으면 null
     */
    public static String getCurrentToken() {
        // 현재 요청(Request)의 HttpServletRequest 객체를 가져옴
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // Authorization 헤더 값 추출 (예: "Bearer ~~~")
        String bearerToken = request.getHeader("Authorization");

        // bearerToken이 비어있지 않고 "Bearer "로 시작하는지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 이후의 실제 토큰 값만 잘라서 반환
            return bearerToken.substring(7);
        }

        // Authorization 헤더가 없거나 형식이 잘못된 경우 null 반환
        return null;
    }
}
