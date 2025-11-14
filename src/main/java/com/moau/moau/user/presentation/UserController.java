// src/main/java/com/moau/moau/user/presentation/UserController.java
package com.moau.moau.user.presentation;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.user.application.UserQueryService;
import com.moau.moau.user.dto.response.UserMeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryService users;
    private final JwtParserPort jwtParser;

    // Authorization 헤더의 Bearer 토큰에서 userId(subject)를 파싱해 현재 사용자 조회
    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new IllegalArgumentException(CommonError.AUTH_HEADER_MISSING.getMessage());
        }
        if (!authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException(CommonError.AUTH_HEADER_INVALID.getMessage());
        }

        String jwt = authorization.substring(7);
        JwtParserPort.Parsed parsed = jwtParser.parse(jwt);
        if (parsed == null || parsed.subject() == null || parsed.subject().isBlank()) {
            throw new IllegalStateException(CommonError.AUTH_SUBJECT_MISSING.getMessage());
        }

        Long userId;
        try {
            userId = Long.parseLong(parsed.subject());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(CommonError.AUTH_SUBJECT_INVALID.getMessage());
        }

        var u = users.getByIdOrThrow(userId);
        return ResponseEntity.ok(UserMeResponse.from(u));
    }
}
