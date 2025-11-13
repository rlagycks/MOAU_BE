// src/main/java/com/moau/moau/token/presentation/LogoutController.java
package com.moau.moau.token.presentation;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.token.application.TokenRefreshService;
import com.moau.moau.token.dto.request.LogoutRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LogoutController {

    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequest req, Authentication auth) {
        if (auth == null || auth.getName() == null) {
            CommonError error = CommonError.LOGOUT_UNAUTHORIZED;

            // 나중에 팀에서 정한 에러 응답 DTO 쓰면 여기서 바꾸면 됨
            return ResponseEntity
                    .status(error.getHttpStatus())
                    .body(error.getMessage());
        }

        tokenRefreshService.revokeByRefreshToken(req.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}