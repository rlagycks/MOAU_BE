package com.moau.moau.user.application.auth;

import com.moau.moau.user.dto.response.auth.kakao.KakaoTokenResponse;
import com.moau.moau.user.dto.response.auth.kakao.KakaoUserMeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 카카오 API 호출 전용 클라이언트
 */
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private final WebClient webClient = WebClient.create();
    private final KakaoProperties kakaoProperties;

    public KakaoTokenResponse exchangeCodeForToken(String code, String codeVerifier, String redirectUri) {
        return webClient.post()
                .uri(kakaoProperties.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=authorization_code"
                        + "&client_id=" + kakaoProperties.restApiKey()
                        + "&redirect_uri=" + redirectUri
                        + "&code=" + code
                        + "&code_verifier=" + codeVerifier)
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();
    }

    public KakaoUserMeResponse getUserInfo(String accessToken) {
        return webClient.get()
                .uri(kakaoProperties.userMeUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserMeResponse.class)
                .block();
    }
}
