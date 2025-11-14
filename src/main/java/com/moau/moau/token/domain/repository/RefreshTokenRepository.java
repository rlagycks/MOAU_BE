// src/main/java/com/moau/moau/token/domain/repository/RefreshTokenRepository.java
package com.moau.moau.token.domain.repository;

import com.moau.moau.token.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByJtiAndRevokedAtIsNull(String jti);
}
