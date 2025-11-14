// src/main/java/com/moau/moau/user/domain/User.java
package com.moau.moau.user.domain;

import com.moau.moau.global.domain.BaseSoftDelete; // created_at, updated_at, deleted_at 포함 가정
import jakarta.persistence.*;
import lombok.*;

/**
 * USERS.id = Kakao user id (외부 PK, AUTO_INCREMENT 금지)
 * email UNIQUE, nickname NOT NULL, status ENUM, soft delete 지원(BaseSoftDelete)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_users_deleted_at", columnList = "deleted_at") // BaseSoftDelete가 deleted_at 매핑한다고 가정
        }
)
public class User extends BaseSoftDelete {

    public enum Status { ACTIVE, SUSPENDED, DEACTIVATED }

    /** PK = 카카오 user id (외부에서 주입, 자동생성 금지) */
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    /** 이메일은 저장 시 소문자 정규화 */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Builder
    private User(Long id, String email, String nickname, Status status) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        if (status != null) this.status = status;
    }

    /** 최초 소셜 가입용 팩토리 */
    public static User createWithKakao(Long kakaoId, String email, String nickname) {
        return User.builder()
                .id(kakaoId)
                .email(email)
                .nickname(nickname != null ? nickname : ("user_" + kakaoId))
                .status(Status.ACTIVE)
                .build();
    }

    // 업데이트용 도메인 메서드
    public void changeNickname(String nickname) { this.nickname = nickname; }
    public void changeEmail(String email) { this.email = email; }
    public void suspend() { this.status = Status.SUSPENDED; }
    public void deactivate() { this.status = Status.DEACTIVATED; }

    /** 저장/업데이트 시 정규화 */
    @PrePersist @PreUpdate
    private void normalize() {
        if (this.email != null) this.email = this.email.trim().toLowerCase();
        if (this.nickname != null) this.nickname = this.nickname.trim();
    }
}
