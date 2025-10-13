package com.moau.moau.jwt.infra;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class SafeKeys {

    public static SecretKey from(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("security.jwt.secret 이 비어있습니다.");
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        // 1) 만약 Base64/URL-Safe Base64 형태라면 디코딩 시도
        try {
            byte[] b1 = Base64.getDecoder().decode(secret);
            if (b1.length >= 32) return Keys.hmacShaKeyFor(b1);
        } catch (IllegalArgumentException ignore) { /* not base64 */ }

        try {
            byte[] b2 = Base64.getUrlDecoder().decode(secret);
            if (b2.length >= 32) return Keys.hmacShaKeyFor(b2);
        } catch (IllegalArgumentException ignore) { /* not base64url */ }

        // 2) 그냥 평문 문자열이면 길이 체크 후 부족하면 SHA-256으로 확장
        if (keyBytes.length < 32) {
            try {
                MessageDigest sha = MessageDigest.getInstance("SHA-256");
                keyBytes = sha.digest(keyBytes); // 32바이트 확보
            } catch (Exception e) {
                throw new RuntimeException("SHA-256 처리 실패", e);
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
