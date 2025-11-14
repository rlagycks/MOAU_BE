package com.moau.moau.token.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * ✅ 토큰 재발급 요청 DTO
 * 클라이언트가 보낸 refreshToken 값을 서버로 전달할 때 사용.
 * 예시 요청:
 *   POST /api/auth/refresh
 *   {
 *     "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
 *   }
 */
public class RefreshRequest {

    // 🔹 refreshToken은 반드시 비어있으면 안 됨.
    //    - @NotBlank: null, "", "   " (공백만 있는 문자열) 모두 허용 안 함.
    @NotBlank(message = "refreshToken은 필수 값입니다.")
    private String refreshToken;

    // 🔹 JSON 역직렬화를 위해 기본 생성자 + getter/setter 필요
    public RefreshRequest() {}

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
