package com.moau.moau.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestDto(
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(max = 500, message = "댓글은 500자 이내여야 합니다.")
        String content,

        @NotNull(message = "익명 여부는 필수입니다.")
        Boolean isAnonymous,

        Long parentId
) {
}