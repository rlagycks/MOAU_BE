package com.moau.moau.auth.dto.response;

import java.time.Instant;

public record CodeExchangeResponse(
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        Long userId,
        String nickname
) {}
