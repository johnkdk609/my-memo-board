package com.mymemo.backend.global.util;

import org.springframework.security.core.context.SecurityContextHolder;

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
}
