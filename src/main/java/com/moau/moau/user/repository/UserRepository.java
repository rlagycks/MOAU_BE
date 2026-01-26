// src/main/java/com/moau/moau/user/repository/UserRepository.java
package com.moau.moau.user.repository;

import com.moau.moau.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 카카오 id로 조회 (이제 가장 중요한 메서드)
    Optional<User> findByKakaoId(Long kakaoId);

    // 이메일로 조회 (필요시 사용)
    Optional<User> findByEmail(String email);

    // soft delete 안 된 유저만 조회
    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    // N+1 쿼리 방지: 여러 유저 일괄 조회
    List<User> findAllByIdIn(Collection<Long> ids);
}
