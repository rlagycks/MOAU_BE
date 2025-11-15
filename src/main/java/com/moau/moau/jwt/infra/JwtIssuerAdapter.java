package com.moau.moau.jwt.infra;

import com.moau.moau.jwt.ports.JwtIssuerPort;
import com.moau.moau.jwt.config.JwtProps;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

// JwtIssuerAdapter.java (불필요한 buildKey 삭제 버전)
public class JwtIssuerAdapter implements JwtIssuerPort {
    private final SecretKey key;
    private final JwtProps props;

    public JwtIssuerAdapter(JwtProps props) {
        this.props = props;
        this.key = SafeKeys.from(props.getSecret());
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
