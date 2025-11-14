package com.moau.moau.token.domain;

import com.moau.moau.global.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "REFRESH_TOKENS")
public class RefreshToken extends BaseTime { // created_at, updated_at 매핑

    @Id
    @Column(name = "jti", nullable = false, length = 36) // UUID 기준 36
    private String jti;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // VARBINARY: 전체 RT 문자열의 해시(SHA-256 → 32 bytes)
    @Column(name = "token_hash", nullable = false, length = 32)
    private byte[] tokenHash;

    // null이면 유효, 값이 있으면 폐기(회전/로그아웃 등)
    @Column(name = "revoked_at")
    private Instant revokedAt;

    /** 생성 팩토리 */
    public static RefreshToken of(String jti, Long userId, byte[] tokenHash) {
        RefreshToken rt = new RefreshToken();
        rt.jti = jti;
        rt.userId = userId;
        rt.tokenHash = tokenHash;
        return rt;
    }

    /** 폐기(회전/로그아웃) */
    public void revokeNow() {
        this.revokedAt = Instant.now();
    }

    /** 활성(미폐기) 여부 */
    public boolean isActive() {
        return revokedAt == null;
    }
}
