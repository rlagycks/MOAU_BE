package com.moau.moau.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * SecurityContextHolder에서 현재 인증된 사용자의 ID를 가져옵니다.
     *
     * @return Long 타입의 유저 ID
     * @throws RuntimeException 인증 정보가 없거나, 유저 ID가 Long 타입이 아닐 경우
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        try {
            // JwtAuthenticationFilter에서 'parsed.subject()' (String 타입의 ID)를 principal로 저장했으므로,
            // Long 타입으로 변환하여 반환합니다.
            return Long.parseLong((String) authentication.getPrincipal());
        } catch (NumberFormatException | ClassCastException e) {
            throw new RuntimeException("인증 정보에서 유저 ID를 파싱할 수 없습니다.");
        }
    }
}