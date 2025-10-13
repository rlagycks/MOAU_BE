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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtParserPort jwtParser) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 🔓 인증 없이 열어둘 경로
                        .requestMatchers(
                                "/dev/auth/**",        // 개발용 발급
                                "/api/auth/refresh",   // RT 재발급
                                "/api/auth/logout",    // 로그아웃
                                "/login/oauth2/**",    // 카카오 콜백
                                "/actuator/health"
                        ).permitAll()
                        // 🔐 그 외 /api/** 는 AT 필요
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )

                // JWT 필터 등록 (폼로그인/세션 안씀)
                .addFilterBefore(new JwtAuthenticationFilter(jwtParser), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
