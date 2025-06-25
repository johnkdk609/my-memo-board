package com.mymemo.backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TokenLogoutRequestDto {
    @Schema(description = "로그아웃 대상 사용자 이메일", example = "user@example.com")
    private String email;
}
