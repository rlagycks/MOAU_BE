// src/main/java/com/moau/moau/token/presentation/TokenController.java
package com.moau.moau.token.controller;

import com.moau.moau.auth.controller.AuthControllerSwagger;
import com.moau.moau.token.service.TokenRefreshService;
import com.moau.moau.token.dto.request.RefreshRequest;
import com.moau.moau.token.dto.response.RefreshResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "🛡️ Auth")

public class TokenController {

    private final TokenRefreshService tokenRefreshService;

    @AuthControllerSwagger.Refresh
    /** 리프레시 토큰 회전 */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest req) {
        var rotated = tokenRefreshService.rotate(req.getRefreshToken());

        return ResponseEntity.ok(new RefreshResponse(
                rotated.accessToken(),
                rotated.refreshToken(),
                rotated.refreshTokenExpiresAt()
        ));
    }
}
