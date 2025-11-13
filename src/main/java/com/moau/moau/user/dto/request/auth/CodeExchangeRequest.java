// src/main/java/com/moau/moau/user/dto/request/auth/CodeExchangeRequest.java
package com.moau.moau.user.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record CodeExchangeRequest(
        @NotBlank(message = "accessToken은 필수 값입니다.")
        String accessToken
) {}
