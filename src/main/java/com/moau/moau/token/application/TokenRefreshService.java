// src/main/java/com/moau/moau/token/application/TokenRefreshService.java
package com.moau.moau.token.application;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtIssuerPort;
import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.token.domain.RefreshToken;
import com.moau.moau.token.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtParserPort jwtParser;
    private final JwtIssuerPort jwtIssuer;
    private final RefreshTokenRepository refreshTokens;

    /** 로그인 직후: 새 RT를 저장(혹은 회전 정책에 맞게 기존 것 폐기) */
    @Transactional
    public void storeNewRefresh(Long userId, String token, String jti) {
        byte[] hash = sha256(token);
        RefreshToken rt = RefreshToken.of(jti, userId, hash);
        refreshTokens.save(rt);
    }

    /** 회전: RT로 새 AT/RT 발급 + 기존 RT 폐기 */
    @Transactional
    public Rotated rotate(String refreshTokenRaw) {
        var parsed = jwtParser.parse(refreshTokenRaw);

        if (!"RT".equals(parsed.typ())) {
            throw new IllegalStateException(CommonError.REFRESH_TOKEN_INVALID_TYPE.getMessage());
        }
        if (parsed.expiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException(CommonError.REFRESH_TOKEN_EXPIRED.getMessage());
        }

        String jti = parsed.jti();
        Long userId = Long.parseLong(parsed.subject());

        var rt = refreshTokens.findByJtiAndRevokedAtIsNull(jti)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.REFRESH_TOKEN_NOT_FOUND.getMessage())
                );

        // 토큰 해시 일치 여부 확인
        byte[] incoming = sha256(refreshTokenRaw);
        if (!MessageDigest.isEqual(incoming, rt.getTokenHash())) {
            throw new IllegalStateException(CommonError.REFRESH_TOKEN_MISMATCH.getMessage());
        }

        // 기존 RT 폐기
        rt.revokeNow();

        // 새 AT/RT 발급
        var at = jwtIssuer.issueAccess(userId, "ROLE_USER");
        var newRt = jwtIssuer.issueRefresh(userId);

        // 새 RT 저장
        storeNewRefresh(userId, newRt.token(), newRt.jti());

        return new Rotated(
                at.token(), at.expiresAt(),
                newRt.token(), newRt.expiresAt()
        );
    }

    /** 로그아웃: 해당 RT를 폐기(또는 사용자 전체 폐기도 가능) */
    @Transactional
    public void revokeByRefreshToken(String refreshTokenRaw) {
        var parsed = jwtParser.parse(refreshTokenRaw);
        if (!"RT".equals(parsed.typ())) {
            throw new IllegalStateException(CommonError.REFRESH_TOKEN_INVALID_TYPE.getMessage());
        }

        var rt = refreshTokens.findByJtiAndRevokedAtIsNull(parsed.jti())
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.REFRESH_TOKEN_NOT_FOUND.getMessage())
                );

        // 해시 검증
        byte[] incoming = sha256(refreshTokenRaw);
        if (!MessageDigest.isEqual(incoming, rt.getTokenHash())) {
            throw new IllegalStateException(CommonError.REFRESH_TOKEN_MISMATCH.getMessage());
        }

        rt.revokeNow();
    }

    private static byte[] sha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException(CommonError.TOKEN_HASH_ERROR.getMessage());
        }
    }

    /** 회전 결과 DTO (서비스 내부용) */
    public record Rotated(
            String accessToken, java.time.Instant accessTokenExpiresAt,
            String refreshToken, java.time.Instant refreshTokenExpiresAt
    ) {}
}

