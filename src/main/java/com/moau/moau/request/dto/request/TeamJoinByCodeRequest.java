// src/main/java/com/moau/moau/team/dto/request/TeamJoinByCodeRequest.java
package com.moau.moau.request.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeamJoinByCodeRequest(
        @NotBlank String inviteCode
) {
}
