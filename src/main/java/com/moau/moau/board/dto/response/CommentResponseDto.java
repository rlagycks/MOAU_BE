package com.moau.moau.board.dto.response;

import java.time.Instant;

public record CommentResponseDto(
        Long commentId,
        Long parentId,
        String content,
        String authorName,
        boolean isMyComment,
        Instant createdAt
) {
}