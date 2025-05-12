package com.mymemo.backend.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration      // @Configuration으로 등록돼 있기 때문에 프로젝트가 실행되면서 springdoc-openapi가 자동으로 인식한다.
@SecurityScheme(
        name = "Authorization",             // Swagger 상에서 이름으로 사용
        type = SecuritySchemeType.HTTP,     // HTTP 기반 인증임을 명시
        scheme = "bearer",                  // Bearer 토큰 방식
        bearerFormat = "JWT"                // 포맷이 JWT임을 Swagger에 설명
)
public class OpenApiConfig {
}
