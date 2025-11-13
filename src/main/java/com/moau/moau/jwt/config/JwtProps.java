// src/main/java/com/moau/moau/jwt/config/JwtProps.java
package com.moau.moau.jwt.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@Configuration
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtProps {

    @NotBlank(message = "JWT 시크릿이 설정되어 있지 않습니다.")
    private String secret;

    @Min(value = 60_000, message = "Access Token 만료(ms)는 60,000 이상이어야 합니다.")
    private long accessTokenExpirationMsec;

    @Min(value = 60_000, message = "Refresh Token 만료(ms)는 60,000 이상이어야 합니다.")
    private long refreshTokenExpirationMsec;

    public void setSecret(String v) { this.secret = v; }

    public void setAccessTokenExpirationMsec(long v) { this.accessTokenExpirationMsec = v; }

    public void setRefreshTokenExpirationMsec(long v) { this.refreshTokenExpirationMsec = v; }
}
