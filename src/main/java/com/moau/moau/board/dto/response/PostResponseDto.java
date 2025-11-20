package com.moau.moau.board.dto.response;

import java.time.Instant;

public record PostResponseDto(
        Long postId,
        String title,
        String contentPreview,
        String authorName,
        int commentCount,
        Instant createdAt
) {
}