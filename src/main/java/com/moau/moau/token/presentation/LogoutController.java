package com.moau.moau.token.presentation;

import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.token.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;

import static com.moau.moau.jwt.infra.TokenHash.sha256;

/**
 * 로그아웃 (Access + Refresh 동시 검증)
 * 요구사항:
 *   Header: Authorization: Bearer <AccessToken>
 *   Body: { "refreshToken": "<현재 Refresh Token>" }
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogoutController {

    private final JwtParserPort jwtParser;
    private final RefreshTokenRepository repo;

    public record LogoutRequest(String refreshToken) {}

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody LogoutRequest body
    ) {
        // 1️⃣ AT 헤더 검증
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String accessToken = authHeader.substring(7);

        try {
            // 2️⃣ AT 검증
            var at = jwtParser.parse(accessToken);
            if (!"AT".equals(at.typ())) {
                return ResponseEntity.status(401).build();
            }

            // 3️⃣ RT 검증 및 폐기
            var rt = jwtParser.parse(body.refreshToken());
            if (!"RT".equals(rt.typ())) {
                return ResponseEntity.status(401).build();
            }

            // DB 존재 확인 + 무결성 체크 후 폐기
            repo.findById(rt.jti()).ifPresent(row -> {
                if (MessageDigest.isEqual(sha256(body.refreshToken()), row.getTokenHash())) {
                    row.revokeNow();
                    repo.save(row);
                }
            });

            // 4️⃣ 정상 로그아웃
            return ResponseEntity.noContent().build(); // 204 No Content

        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).build(); // 토큰 만료/서명 오류 시
        }
    }
}
