package com.moau.moau.jwt.infra;

import com.moau.moau.jwt.ports.JwtIssuerPort;
import com.moau.moau.jwt.config.JwtProps;
import static com.moau.moau.jwt.infra.SafeKeys.from;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class JwtIssuerAdapter implements JwtIssuerPort {

    private final SecretKey key;
    private final JwtProps props;

    public JwtIssuerAdapter(JwtProps props) {
        this.props = props;
        this.key = from(props.getSecret()); // ✅ SafeKeys 사용
        // 디버그: 실행시 콘솔에 키 바이트 길이 출력 (확인용)
        System.out.println("[JWT] issuer key byte length = " + key.getEncoded().length);
    }

    private static SecretKey buildKey(String secret) {
        if (secret == null) throw new IllegalArgumentException("JWT secret is not set");
        byte[] bytes;
        if (secret.startsWith("base64:")) {
            bytes = Decoders.BASE64.decode(secret.substring("base64:".length()));
        } else {
            bytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        // Keys.hmacShaKeyFor will validate length (>= 32 bytes for HS256)
        return Keys.hmacShaKeyFor(bytes);
    }

    @Override
    public IssuedAccess issueAccess(Long userId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(props.getAccessTokenExpirationMsec());
        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("typ", "AT")
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        return new IssuedAccess(token, exp);
    }

    @Override
    public IssuedRefresh issueRefresh(Long userId) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(props.getRefreshTokenExpirationMsec());
        String jti = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("typ", "RT")
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        return new IssuedRefresh(token, jti, exp);
    }
}
