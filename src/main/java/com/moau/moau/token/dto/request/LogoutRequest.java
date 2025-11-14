// src/main/java/com/moau/moau/token/dto/request/LogoutRequest.java
package com.moau.moau.token.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {
    @NotBlank(message = "refreshToken은 필수 값입니다.")
    private String refreshToken;

    public LogoutRequest() {}
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
