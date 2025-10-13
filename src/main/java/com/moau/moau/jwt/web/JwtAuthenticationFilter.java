package com.moau.moau.jwt.web;

import com.moau.moau.jwt.ports.JwtParserPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParserPort jwtParser;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res); // 토큰 없으면 인증 없이 진행 -> 보호 리소스는 아래에서 401
            return;
        }

        String token = header.substring(7);
        try {
            var p = jwtParser.parse(token); // typ, jti, subject, expiresAt
            if (!"AT".equals(p.typ())) {
                chain.doFilter(req, res);
                return;
            }
            var auth = new UsernamePasswordAuthenticationToken(
                    p.subject(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (IllegalStateException ignore) {
            // 만료/서명오류 시 인증 미설정 -> 보호 리소스 접근하면 401
        }

        chain.doFilter(req, res);
    }
}
