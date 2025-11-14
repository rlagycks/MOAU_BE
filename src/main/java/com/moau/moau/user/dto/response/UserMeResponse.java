// src/main/java/com/moau/moau/user/dto/response/UserMeResponse.java
package com.moau.moau.user.dto.response;

import com.moau.moau.user.domain.User;

public record UserMeResponse(Long id, String email, String nickname, String status) {
    public static UserMeResponse from(User u) {
        return new UserMeResponse(u.getId(), u.getEmail(), u.getNickname(), u.getStatus().name());
    }
}
