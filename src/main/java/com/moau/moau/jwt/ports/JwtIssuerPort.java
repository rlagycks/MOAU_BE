package com.moau.moau.jwt.ports;

import java.time.Instant;

public interface JwtIssuerPort {
    record IssuedAccess(String token, Instant expiresAt) {}
    record IssuedRefresh(String token, String jti, Instant expiresAt) {}


    IssuedAccess issueAccess(Long userId, String role);
    IssuedRefresh issueRefresh(Long userId);
}