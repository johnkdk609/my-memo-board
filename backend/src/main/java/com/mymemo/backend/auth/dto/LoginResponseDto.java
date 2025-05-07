package com.mymemo.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor     // 응답을 만들 때 직접 new LoginResponseDto(token) 형태로 사용하기 때문에, 모든 필드를 한 번에 초기화하는 생성자가 필요하다.
public class LoginResponseDto {
    private String accessToken;
}
