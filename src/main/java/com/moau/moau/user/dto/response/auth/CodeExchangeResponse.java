package com.moau.moau.user.dto.response.auth;

import java.time.Instant;

public record CodeExchangeResponse(
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        Long userId,
        String nickname
) {}
