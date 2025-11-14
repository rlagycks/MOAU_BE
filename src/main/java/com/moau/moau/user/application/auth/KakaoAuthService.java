// src/main/java/com/moau/moau/user/application/auth/KakaoAuthService.java
package com.moau.moau.user.application.auth;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtIssuerPort;
import com.moau.moau.token.application.TokenRefreshService;
import com.moau.moau.user.application.UserCommandService;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.dto.response.auth.CodeExchangeResponse;
import com.moau.moau.user.dto.response.auth.kakao.KakaoUserMeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final UserCommandService userCommandService;
    private final JwtIssuerPort jwtIssuerPort;
    private final TokenRefreshService tokenRefreshService;

    @Transactional
    public CodeExchangeResponse exchange(String accessToken) {
        // 1) 카카오 유저 정보 조회
        KakaoUserMeResponse kakaoUser = kakaoOAuthClient.getUserInfo(accessToken);
        if (kakaoUser == null) {
            throw new IllegalStateException(CommonError.KAKAO_USERINFO_NOT_FOUND.getMessage());
        }
        if (kakaoUser.kakaoAccount() == null || kakaoUser.kakaoAccount().email() == null) {
            throw new IllegalStateException(CommonError.KAKAO_EMAIL_NOT_PROVIDED.getMessage());
        }

        // 2) 사용자 upsert
        UserCommandService.UpsertResult result = userCommandService.upsertKakaoUser(
                kakaoUser.id(),
                kakaoUser.kakaoAccount().email(),
                kakaoUser.kakaoAccount().profile().nickname()
        );

        User user = result.user();

        // 3) JWT 발급
        var access = jwtIssuerPort.issueAccess(user.getId(), "USER"); // 혹시 ROLE_ 붙여야 하면 여기만 수정
        var refresh = jwtIssuerPort.issueRefresh(user.getId());

        // 4) 리프레시 토큰 저장 (공통 서비스 사용)
        tokenRefreshService.storeNewRefresh(user.getId(), refresh.token(), refresh.jti());

        // 5) 응답 반환
        return new CodeExchangeResponse(
                access.token(),
                access.expiresAt(),
                refresh.token(),
                refresh.expiresAt(),
                user.getId(),
                user.getNickname()
        );
    }
}
