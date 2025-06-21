package com.mymemo.backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TokenReissueRequestDto {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "요청에 포함된 Refresh Token", example = "aaa.bbb.ccc")
    private String refreshToken;
}
