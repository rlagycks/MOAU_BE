// src/main/java/com/moau/moau/auth/service/KakaoAuthService.java
package com.moau.moau.auth.service;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtIssuerPort;
import com.moau.moau.token.service.TokenRefreshService;
import com.moau.moau.user.service.UserCommandService;
import com.moau.moau.user.domain.User;
import com.moau.moau.auth.dto.response.CodeExchangeResponse;
import com.moau.moau.auth.dto.response.kakao.KakaoUserMeResponse;
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

        // kakao_account 널 체크
        if (kakaoUser.kakaoAccount() == null) {
            throw new IllegalStateException(CommonError.KAKAO_USERINFO_NOT_FOUND.getMessage());
        }

        // 이메일 필수 체크
        String email = kakaoUser.kakaoAccount().email();
        if (email == null || email.isBlank()) {
            throw new IllegalStateException(CommonError.KAKAO_EMAIL_NOT_PROVIDED.getMessage());
        }

        // 닉네임 널/빈 문자열 안전하게 처리
        String nickname;
        if (kakaoUser.kakaoAccount().profile() != null
                && kakaoUser.kakaoAccount().profile().nickname() != null
                && !kakaoUser.kakaoAccount().profile().nickname().isBlank()) {
            nickname = kakaoUser.kakaoAccount().profile().nickname();
        } else {
            // profile 이 없거나 nickname 이 비어 있으면 기본값 사용
            nickname = "카카오유저";
        }

        // 2) 사용자 upsert
        UserCommandService.UpsertResult result = userCommandService.upsertKakaoUser(
                kakaoUser.id(),
                email,
                nickname
        );

        User user = result.user();

        // 3) JWT 발급
        var access = jwtIssuerPort.issueAccess(user.getId(), "USER"); // 역할 이름 필요하면 여기 조정
        var refresh = jwtIssuerPort.issueRefresh(user.getId());

        // 4) 리프레시 토큰 저장
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
