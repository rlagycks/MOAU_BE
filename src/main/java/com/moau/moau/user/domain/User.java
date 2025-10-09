package com.moau.moau.user.domain;

import com.moau.moau.global.domain.BaseSoftDelete;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자를 필요로 합니다.
@Entity
@Table(name = "USERS")
public class User extends BaseSoftDelete {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    // ERD의 status 컬럼 예시. 실제 Enum 클래스는 별도로 정의해야 합니다.
    // @Enumerated(EnumType.STRING)
    // @Column(nullable = false)
    // private UserStatus status;
}