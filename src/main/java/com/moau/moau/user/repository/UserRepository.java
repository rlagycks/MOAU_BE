// src/main/java/com/moau/moau/user/domain/repository/UserRepository.java
package com.moau.moau.user.repository;

import com.moau.moau.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // soft delete 안 된 유저만 조회 (User에 deletedAt 필드 있다고 가정)
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    // 카카오용 UPSERT (MySQL 기준)
    @Modifying
    @Query(value = """
            INSERT INTO USERS (id, email, nickname)
            VALUES (:id, :email, :nickname)
            ON DUPLICATE KEY UPDATE
                email = VALUES(email),
                nickname = VALUES(nickname)
            """, nativeQuery = true)
    int upsertKakao(
            @Param("id") long kakaoId,
            @Param("email") String email,
            @Param("nickname") String nickname
    );
}
