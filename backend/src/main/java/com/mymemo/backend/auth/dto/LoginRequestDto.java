package com.mymemo.backend.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor      // JSON 요청 바디를 @RequestBody로 바인딩할 때, Jackson이 기본 생성자 + setter or field 접근으로 값을 주입한다. 이때 기본 생성자가 없으면 바인딩 실패함.
public class LoginRequestDto {
    private String email;
    private String password;
}
