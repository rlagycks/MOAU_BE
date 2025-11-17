// src/main/java/com/moau/moau/user/repository/UserRepository.java
package com.moau.moau.user.repository;

import com.moau.moau.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 카카오 id로 조회 (이제 가장 중요한 메서드)
    Optional<User> findByKakaoId(Long kakaoId);

    // 이메일로 조회 (필요시 사용)
    Optional<User> findByEmail(String email);

    // soft delete 안 된 유저만 조회
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
}
