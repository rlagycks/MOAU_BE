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
                // CSRF는 REST API라 비활성화
                .csrf(csrf -> csrf.disable())
                // CORS 설정은 CorsConfig에서 세부 지정
                .cors(Customizer.withDefaults())
                // 세션은 완전히 비활성화 (JWT만 사용)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        //  앱 교환용 로그인/토큰 회전은 인증 없이 허용
                        .requestMatchers(
                                "/api/auth/kakao/code/exchange",
                                "/api/auth/refresh",
                                "/actuator/health",
                                "/favicon.ico"
                        ).permitAll()
                        //  로그아웃은 인증된 사용자만 가능하도록
                        .requestMatchers("/api/auth/logout").authenticated()
                        //  나머지 /api/** 는 전부 JWT 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        // 그 외는 전부 허용 (문서, 정적 리소스 등)
                        .anyRequest().permitAll()
                )
                //  불필요한 oauth2Login 제거 (앱 교환 방식에서는 사용하지 않음)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
