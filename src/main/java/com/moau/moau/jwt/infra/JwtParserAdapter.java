package com.moau.moau.jwt.infra;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.jwt.config.JwtProps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

// JwtParserAdapter.java (불필요한 buildKey 삭제 버전)
public class JwtParserAdapter implements JwtParserPort {
    private final SecretKey key;

    public JwtParserAdapter(JwtProps props) {
        this.key = SafeKeys.from(props.getSecret());
        System.out.println("[JWT] parser key byte length = " + key.getEncoded().length);
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
            // 토큰 만료
            throw new IllegalStateException(CommonError.TOKEN_EXPIRED.getMessage(), e);
        } catch (JwtException e) {
            // 서명 불일치, 파싱 실패 등 모든 토큰 유효성 문제
            throw new IllegalStateException(CommonError.TOKEN_INVALID.getMessage(), e);
        }
    }
}