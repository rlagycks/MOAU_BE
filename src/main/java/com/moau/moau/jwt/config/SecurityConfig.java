package com.moau.moau.jwt.config;

import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.jwt.web.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtParserPort parser) {
        return new JwtAuthenticationFilter(parser);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter
    ) throws Exception {

        http
                // 1. (추천) httpBasic, formLogin 비활성화
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())

                // CSRF는 REST API라 비활성화
                .csrf(csrf -> csrf.disable())
                // CORS 설정은 CorsConfig에서 세부 지정
                .cors(Customizer.withDefaults())
                // 세션은 완전히 비활성화 (JWT만 사용)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2. (필수) authorizeHttpRequests 수정
                .authorizeHttpRequests(auth -> auth
                        //  (1) 인증 없이 허용할 URL 목록
                        .requestMatchers(
                                // 로그인/토큰 관련
                                "/api/auth/kakao/code/exchange",
                                "/api/auth/refresh",
                                "/api/auth/test/login",

                                // 헬스체크, 파비콘
                                "/actuator/health",
                                "/favicon.ico",

                                // (2) Swagger UI 관련 경로 추가
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // (3) 위에서 허용한 URL을 제외한 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                //  불필요한 oauth2Login 제거 (앱 교환 방식에서는 사용하지 않음)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
