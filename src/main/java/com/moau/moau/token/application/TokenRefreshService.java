package com.moau.moau.token.application;

import com.moau.moau.jwt.ports.JwtParserPort;   // ★ 추가
import com.moau.moau.jwt.ports.JwtIssuerPort;   // ★ 추가

import com.moau.moau.token.domain.RefreshToken;
import com.moau.moau.token.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.Instant;

import static com.moau.moau.jwt.infra.TokenHash.sha256;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {
    private final JwtParserPort jwtParser;
    private final JwtIssuerPort jwtIssuer;
    private final RefreshTokenRepository repo;

    public record Result(String accessToken, String refreshToken, Instant refreshExpiresAt) {}

    public Result refresh(String refreshTokenRaw) {
        if (refreshTokenRaw == null || refreshTokenRaw.isBlank())
            throw new IllegalStateException("리프레시 토큰이 필요합니다.");

        JwtParserPort.Parsed p = jwtParser.parse(refreshTokenRaw);
        if (!"RT".equals(p.typ())) throw new IllegalStateException("유효하지 않은 토큰 타입입니다.");
        String jti = p.jti();
        Long userId = Long.valueOf(p.subject());

        RefreshToken row = repo.findById(jti)
                .orElseThrow(() -> new IllegalStateException("리프레시 토큰이 유효하지 않습니다."));
        if (!row.isActive()) throw new IllegalStateException("리프레시 토큰이 만료되었거나 폐기되었습니다.");
        if (!MessageDigest.isEqual(sha256(refreshTokenRaw), row.getTokenHash()))
            throw new IllegalStateException("리프레시 토큰 무결성 검증에 실패했습니다.");

        row.revokeNow();
        repo.save(row);

        // 기존 코드 중 새 RT 저장 부분만 수정
        JwtIssuerPort.IssuedAccess at = jwtIssuer.issueAccess(userId, "USER");
        JwtIssuerPort.IssuedRefresh rt = jwtIssuer.issueRefresh(userId);
        repo.save(RefreshToken.of(rt.jti(), userId, sha256(rt.token())));


        return new Result(at.token(), rt.token(), rt.expiresAt());
    }
}
