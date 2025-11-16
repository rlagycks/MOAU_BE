package com.moau.moau.test.controller;

import com.moau.moau.jwt.ports.JwtIssuerPort;
import com.moau.moau.token.service.TokenRefreshService;
import com.moau.moau.user.service.UserCommandService;
import com.moau.moau.user.domain.User;
import com.moau.moau.auth.dto.response.CodeExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile({"dev"})
@RestController
@RequestMapping("/api/auth/test")
@RequiredArgsConstructor
public class TestAuthController {

    private final UserCommandService userCommandService;
    private final JwtIssuerPort jwtIssuerPort;
    private final TokenRefreshService tokenRefreshService;

    @GetMapping("/login")
    public ResponseEntity<CodeExchangeResponse> testLogin(
            @RequestParam(defaultValue = "test@moau.com") String email
    ) {

        long testKakaoId = email.hashCode() & 0xFFFFFFFFL;
        String testNickname = "TestUser-" + email.split("@")[0];

        UserCommandService.UpsertResult result = userCommandService.upsertKakaoUser(
                testKakaoId,
                email,
                testNickname
        );

        User user = result.user();

        var access = jwtIssuerPort.issueAccess(user.getId(), "USER");
        var refresh = jwtIssuerPort.issueRefresh(user.getId());

        tokenRefreshService.storeNewRefresh(user.getId(), refresh.token(), refresh.jti());

        return ResponseEntity.ok(new CodeExchangeResponse(
                access.token(),
                access.expiresAt(),
                refresh.token(),
                refresh.expiresAt(),
                user.getId(),
                user.getNickname()
        ));
    }
}