package com.moau.moau.jwt.ports;

import java.time.Instant;

public interface JwtParserPort {
    record Parsed(String typ, String jti, String subject, Instant expiresAt) {}
    Parsed parse(String jwt);
}