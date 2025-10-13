package com.moau.moau.jwt.config;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "security.jwt") // ★ 기존 YAML 트리에 맞춤
public class JwtProps {
    private String secret;                     // security.jwt.secret
    private long accessTokenExpirationMsec;    // security.jwt.access-token-expiration-msec
    private long refreshTokenExpirationMsec;   // security.jwt.refresh-token-expiration-msec

    public void setSecret(String v) { this.secret = v; }
    public void setAccessTokenExpirationMsec(long v) { this.accessTokenExpirationMsec = v; }
    public void setRefreshTokenExpirationMsec(long v) { this.refreshTokenExpirationMsec = v; }
}
