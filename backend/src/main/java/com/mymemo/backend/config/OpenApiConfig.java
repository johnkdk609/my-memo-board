package com.mymemo.backend.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration      // @Configuration으로 등록돼 있기 때문에 프로젝트가 실행되면서 springdoc-openapi가 자동으로 인식한다.
@SecurityScheme(
        name = "Authorization",             // Swagger 상에서 이름으로 사용
        type = SecuritySchemeType.HTTP,     // HTTP 기반 인증임을 명시
        scheme = "bearer",                  // Bearer 토큰 방식
        bearerFormat = "JWT"                // 포맷이 JWT임을 Swagger에 설명
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("MyMemo API").version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}
