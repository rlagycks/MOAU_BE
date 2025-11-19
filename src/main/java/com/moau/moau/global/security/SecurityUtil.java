package com.moau.moau.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        // 1. SecurityContextHolder에서 Authentication 객체를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 인증 정보가 없거나, 인증되지 않은 경우 예외 처리
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("SecurityContext에 인증 정보가 없습니다.");
        }

        // 3. Principal(사용자 ID)을 가져옵니다.
        Object principal = authentication.getPrincipal();

        // 4. JwtAuthenticationFilter에서 String(parsed.subject()) 또는 Long으로 저장했을 것입니다.
        if (principal instanceof String) {
            try {
                // String을 Long으로 변환
                return Long.parseLong((String) principal);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Principal이 Long 타입으로 변환될 수 없는 String입니다.");
            }
        } else if (principal instanceof Long) {
            // 이미 Long 타입인 경우
            return (Long) principal;
        }

        // (예상치 못한 타입인 경우)
        throw new IllegalStateException("Principal이 예상된 타입(String 또는 Long)이 아닙니다.");
    }
}