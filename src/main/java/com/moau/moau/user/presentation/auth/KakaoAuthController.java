// src/main/java/com/moau/moau/user/presentation/auth/KakaoAuthController.java
package com.moau.moau.user.presentation.auth;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.user.application.auth.KakaoAuthService;
import com.moau.moau.user.dto.request.auth.CodeExchangeRequest;
import com.moau.moau.user.dto.response.auth.CodeExchangeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    /**
     * 카카오 SDK 로그인 후 받은 accessToken을 서버에 전달
     * POST /api/auth/kakao/code/exchange
     * {
     *   "accessToken": "v1K34f..."
     * }
     */
    @PostMapping("/code/exchange")
    public ResponseEntity<?> exchangeCode(@Valid @RequestBody CodeExchangeRequest request) {
        if (request.accessToken() == null || request.accessToken().isBlank()) {
            var error = CommonError.KAKAO_ACCESS_TOKEN_REQUIRED;
            return ResponseEntity
                    .status(error.getHttpStatus())
                    .body(error.getMessage());
        }

        CodeExchangeResponse response = kakaoAuthService.exchange(request.accessToken());
        return ResponseEntity.ok(response);
    }
}
