// src/main/java/com/moau/moau/user/presentation/auth/KakaoAuthController.java
package com.moau.moau.auth.controller;

import com.moau.moau.auth.controller.AuthControllerSwagger;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.auth.service.KakaoAuthService;
import com.moau.moau.auth.dto.request.CodeExchangeRequest;
import com.moau.moau.auth.dto.response.CodeExchangeResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
@Tag(name = "🛡️ Auth")

public class KakaoAuthController  {

    private final KakaoAuthService kakaoAuthService;

    /**
     * 카카오 SDK 로그인 후 받은 accessToken을 서버에 전달
     * POST /api/auth/kakao/code/exchange
     * {
     *   "accessToken": "v1K34f..."
     * }
     */
    @AuthControllerSwagger.ExchangeCode
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
