package com.moau.moau.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("MOAU 프로젝트 API") // API 문서 제목
                .version("v1.0.0")       // 버전
                .description("MOAU API 명세서입니다.");

        // 1. SecurityScheme 이름 (임의 지정)
        String jwtSchemeName = "bearerAuth";

        // 2. SecurityScheme 설정 (HTTP Bearer 방식)
        // API 인증을 어떻게 할 것인지 정의합니다.
        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwtSchemeName)            // 스킴 이름
                .type(SecurityScheme.Type.HTTP) // 인증 타입
                .scheme("bearer")               // 스킴: Bearer
                .bearerFormat("JWT");           // 포맷

        // 3. 전역 SecurityRequirement 설정
        // 모든 API 요청에 'bearerAuth' 스킴을 적용하도록 설정합니다.
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // 4. OpenAPI 객체 생성 및 설정
        return new OpenAPI()
                .info(info)
                .components(new Components()
                        // 2번에서 정의한 SecurityScheme을 Components에 추가
                        .addSecuritySchemes(jwtSchemeName, securityScheme))
                // 3번에서 정의한 SecurityRequirement를 전역으로 적용
                .addSecurityItem(securityRequirement);
    }
}