package com.moau.moau.jwt.web;

import com.moau.moau.jwt.ports.JwtParserPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParserPort jwtParser;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 이미 인증된 경우 그대로 진행
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(req, res);
            return;
        }

        // Authorization 헤더에서 Bearer 토큰 추출
        String header = req.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);
        try {
            var parsed = jwtParser.parse(token);

            // Access Token(AT)이 아니면 인증 처리하지 않음
            if (!"AT".equals(parsed.typ())) {
                chain.doFilter(req, res);
                return;
            }

            // 권한 컬렉션은 필요 시 추가 가능 (현재는 없음)
            var auth = new UsernamePasswordAuthenticationToken(
                    parsed.subject(),  // JWT subject = userId
                    null,
                    null
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (IllegalStateException e) {
            // 토큰 만료/서명 오류 → 인증 없이 진행
        }

        chain.doFilter(req, res);
    }
}
