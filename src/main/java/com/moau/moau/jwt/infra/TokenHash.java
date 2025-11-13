package com.moau.moau.jwt.infra;

import com.moau.moau.global.exception.error.CommonError;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class TokenHash {
    private TokenHash() {}

    /** Refresh 원문 문자열 → SHA-256 해시(byte[]) */
    public static byte[] sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException(CommonError.TOKEN_HASH_ERROR.getMessage(), e);
        }
    }
}
