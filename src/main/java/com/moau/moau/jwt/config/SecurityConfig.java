package com.moau.moau.jwt.config;

import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.jwt.web.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtParserPort parser, HandlerExceptionResolver resolver) {
        return new JwtAuthenticationFilter(parser, resolver);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(HandlerExceptionResolver resolver) {
        return (request, response, authException) ->
                resolver.resolveException(request, response, null, authException);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(HandlerExceptionResolver resolver) {
        return (request, response, accessDeniedException) ->
                resolver.resolveException(request, response, null, accessDeniedException);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) throws Exception {

        http
                // 기본 로그인 방식 비활성화
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())

                // REST API에서는 CSRF 사용 안함
                .csrf(csrf -> csrf.disable())

                // CORS는 CorsConfig에서 별도 처리
                .cors(Customizer.withDefaults())

                // 세션 완전 비활성화 (JWT 기반)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // ★ 카카오 로그인 공개 URL
                                "/api/auth/kakao/login",
                                "/api/auth/kakao/callback",
                                "/api/auth/kakao/code/exchange",

                                // 토큰 관련
                                "/api/auth/refresh",
                                "/api/auth/test/login",

                                // Swagger
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",

                                // 헬스 체크
                                "/actuator/health",
                                "/favicon.ico"
                        ).permitAll()

                        // 그 외 모든 API는 인증 필요
                        .anyRequest().authenticated()
                )

                // 인증/인가 실패 시 전역 예외 처리로 위임
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // JWT 필터 등록
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
