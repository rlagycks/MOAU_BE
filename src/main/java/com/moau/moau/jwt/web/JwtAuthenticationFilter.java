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
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParserPort jwtParser;
    private final HandlerExceptionResolver resolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/api/auth/kakao")
                || path.equals("/api/auth/refresh")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/actuator/health");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);

        try {
            var parsed = jwtParser.parse(token);

            if (!"AT".equals(parsed.typ())) {
                chain.doFilter(req, res);
                return;
            }

            var auth = new UsernamePasswordAuthenticationToken(
                    parsed.subject(),
                    null,
                    null
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            chain.doFilter(req, res);
        } catch (Exception e) {
            resolver.resolveException(req, res, null, e);
        }
    }
}
