package com.moau.moau.token.presentation;

import com.moau.moau.token.application.TokenRefreshService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/**
 * 토큰 재발급 (RefreshToken → 새 AccessToken + RefreshToken)
 * 요청 예시:
 *   POST /api/auth/refresh
 *   Body: { "refreshToken": "eyJhbGciOi..." }
 * 응답:
 *   200 OK : 새 AT/RT 발급
 *   401 Unauthorized : RT 만료 or 위조
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class TokenController {

    private final TokenRefreshService refreshService;

    /** 요청 DTO */
    public record RefreshRequest(@NotBlank String refreshToken) {}

    /** 응답 DTO */
    public record RefreshResponse(String accessToken, String refreshToken, Instant refreshExpiresAt) {}

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest req) {
        try {
            var r = refreshService.refresh(req.refreshToken());
            return ResponseEntity.ok(new RefreshResponse(r.accessToken(), r.refreshToken(), r.refreshExpiresAt()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
