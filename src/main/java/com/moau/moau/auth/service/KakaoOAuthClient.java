package com.moau.moau.auth.service;

import com.moau.moau.auth.dto.response.kakao.KakaoTokenResponse;
import com.moau.moau.auth.dto.response.kakao.KakaoUserMeResponse;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 카카오 API 호출 전용 클라이언트
 */
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private final WebClient webClient;
    private final KakaoProperties kakaoProperties;

    public KakaoTokenResponse exchangeCodeForToken(String code, String codeVerifier, String redirectUri) {
        try {
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
        } catch (WebClientResponseException e) {
            throw new BusinessException(CommonError.EXTERNAL_API_ERROR, e.getResponseBodyAsString(), e);
        }
    }

    public KakaoUserMeResponse getUserInfo(String accessToken) {
        try {
            return webClient.get()
                    .uri(kakaoProperties.userMeUri())
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(KakaoUserMeResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new BusinessException(CommonError.EXTERNAL_API_ERROR, e.getResponseBodyAsString(), e);
        }
    }
}
