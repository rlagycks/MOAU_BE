// src/main/java/com/moau/moau/user/application/auth/KakaoProperties.java
package com.moau.moau.user.application.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app.kakao")
public record KakaoProperties(
        @NotBlank String restApiKey,
        @NotBlank String tokenUri,
        @NotBlank String userMeUri,
        @NotEmpty List<String> redirectUris
) {}
