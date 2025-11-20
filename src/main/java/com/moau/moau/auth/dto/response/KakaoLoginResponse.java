// src/main/java/com/moau/moau/user/dto/response/auth/KakaoLoginResponse.java
package com.moau.moau.auth.dto.response;

public record KakaoLoginResponse(String accessToken, String refreshToken, boolean isNewUser) {}
