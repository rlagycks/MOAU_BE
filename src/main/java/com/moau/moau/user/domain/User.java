// src/main/java/com/moau/moau/user/domain/User.java
package com.moau.moau.user.domain;

import com.moau.moau.global.domain.BaseSoftDelete;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_kakao_id", columnNames = "kakao_id")
        },
        indexes = {
                @Index(name = "idx_users_deleted_at", columnList = "deleted_at"),
                @Index(name = "idx_users_kakao_id", columnList = "kakao_id")
        }
)
public class User extends BaseSoftDelete {

    public enum Status { ACTIVE, SUSPENDED, DEACTIVATED }

    /** 서비스 내부 PK (AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** 카카오에서 내려오는 user id (외부 식별자, PK 아님) */
    @Column(name = "kakao_id", nullable = false)
    private Long kakaoId;

    /** 이메일은 저장 시 소문자 정규화 */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Builder
    private User(Long kakaoId, String email, String nickname, Status status) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.nickname = nickname;
        if (status != null) {
            this.status = status;
        }
    }

    /** 최초 소셜 가입용 팩토리 */
    public static User createWithKakao(Long kakaoId, String email, String nickname) {
        return User.builder()
                .kakaoId(kakaoId)
                .email(email)
                .nickname(nickname != null ? nickname : ("user_" + kakaoId))
                .status(Status.ACTIVE)
                .build();
    }

    // 업데이트용 도메인 메서드
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void suspend() {
        this.status = Status.SUSPENDED;
    }

    public void deactivate() {
        this.status = Status.DEACTIVATED;
    }

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }
        if (this.nickname != null) {
            this.nickname = this.nickname.trim();
        }
    }
}
