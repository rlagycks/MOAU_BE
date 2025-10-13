package com.moau.moau.jwt.infra;

import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.jwt.config.JwtProps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import static com.moau.moau.jwt.infra.SafeKeys.from;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class JwtParserAdapter implements JwtParserPort {

    private final SecretKey key;

    public JwtParserAdapter(JwtProps props) {
        this.key = from(props.getSecret()); // ✅ SafeKeys 사용
        // 디버그: 실행시 콘솔에 키 바이트 길이 출력 (확인용)
        System.out.println("[JWT] parser key byte length = " + key.getEncoded().length);
    }

    private static SecretKey buildKey(String secret) {
        if (secret == null) throw new IllegalArgumentException("JWT secret is not set");
        byte[] bytes;
        if (secret.startsWith("base64:")) {
            bytes = Decoders.BASE64.decode(secret.substring("base64:".length()));
        } else {
            bytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    @Override
    public Parsed parse(String jwt) {
        try {
            Claims c = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            return new Parsed(
                    c.get("typ", String.class),
                    c.getId(),
                    c.getSubject(),
                    c.getExpiration().toInstant()
            );
        } catch (ExpiredJwtException e) {
            throw new IllegalStateException("토큰이 만료되었습니다.", e);
        } catch (JwtException e) {
            throw new IllegalStateException("유효하지 않은 토큰입니다.", e);
        }
    }
}
