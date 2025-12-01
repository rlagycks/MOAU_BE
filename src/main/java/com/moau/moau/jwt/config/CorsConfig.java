package com.moau.moau.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 자격 증명(쿠키, 인증 헤더 등) 허용
        config.setAllowCredentials(true);

        // 2. 허용할 출처(Origin) 설정
        //    (예: "http://localhost:3000" - 프론트엔드 주소)
        //    (예: "http://localhost:8080" - Swagger UI가 실행되는 주소)
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("https://moau.store");
        config.addAllowedOrigin("http://localhost:8081");
        // (만약 모든 출처를 허용하려면 config.addAllowedOriginPattern("*");)

        // 3. 허용할 HTTP 메서드 (GET, POST, PUT, DELETE 등)
        config.addAllowedMethod("*");

        // 4. 허용할 HTTP 헤더 (Authorization, Content-Type 등)
        config.addAllowedHeader("*");

        // 5. 모든 경로("/**")에 대해 위 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}