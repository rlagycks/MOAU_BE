package com.moau.moau.token.dto.response;

import java.time.Instant;

/**
 * ✅ 토큰 재발급 응답 DTO
 * 클라이언트에게 새 AccessToken / RefreshToken / 만료시각을 반환.
 * 예시 응답:
 *   {
 *     "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *     "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *     "refreshExpiresAt": "2025-10-13T14:52:03Z"
 *   }
 */
public class RefreshResponse {

    // 🔹 새로 발급된 AccessToken (API 요청 시 Authorization 헤더에 사용)
    private final String accessToken;

    // 🔹 새로 발급된 RefreshToken (다음 재발급 때 사용)
    private final String refreshToken;

    // 🔹 RefreshToken의 만료 시각 (ISO 8601 형식)
    private final Instant refreshExpiresAt;

    // 🔹 모든 필드를 한 번에 설정하는 생성자
    public RefreshResponse(String accessToken, String refreshToken, Instant refreshExpiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshExpiresAt = refreshExpiresAt;
    }

    // 🔹 Getter (응답 직렬화 시 필요)
    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Instant getRefreshExpiresAt() {
        return refreshExpiresAt;
    }
}
