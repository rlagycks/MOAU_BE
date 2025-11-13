// src/main/java/com/moau/moau/token/presentation/TokenController.java
package com.moau.moau.token.presentation;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.token.application.TokenRefreshService;
import com.moau.moau.token.dto.request.RefreshRequest;
import com.moau.moau.token.dto.response.RefreshResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final TokenRefreshService tokenRefreshService;

    /** 리프레시 토큰 회전 */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest req) {
        try {
            var rotated = tokenRefreshService.rotate(req.getRefreshToken());

            return ResponseEntity.ok(new RefreshResponse(
                    rotated.accessToken(),
                    rotated.refreshToken(),
                    rotated.refreshTokenExpiresAt()
            ));
        } catch (IllegalArgumentException e) {
            // 예: rotate 내부에서 잘못된 토큰이면 IllegalArgumentException 던진다고 가정
            var error = CommonError.REFRESH_TOKEN_INVALID;
            return ResponseEntity
                    .status(error.getHttpStatus())
                    .body(error.getMessage());
        }
    }
}