// src/main/java/com/moau/moau/user/dto/request/UserUpdateRequest.java
package com.moau.moau.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @Size(min = 1, max = 50, message = "닉네임은 1자 이상 50자 이하로 입력해주세요.")
        String nickname
) {
}
