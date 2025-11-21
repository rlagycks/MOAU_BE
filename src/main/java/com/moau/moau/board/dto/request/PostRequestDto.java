package com.moau.moau.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRequestDto(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        String content,

        @NotNull(message = "익명 여부는 필수입니다.")
        Boolean isAnonymous
) {
}