package com.moau.moau.auth.dto.response.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserMeResponse(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            String email,
            Profile profile
    ) {}

    public record Profile(
            String nickname,
            @JsonProperty("profile_image_url") String profileImageUrl
    ) {}
}
