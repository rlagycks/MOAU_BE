package com.moau.moau.board.dto.response;

import java.time.Instant;
import java.util.List;

public record PostDetailResponseDto(
        Long postId,
        Long authorId,
        String title,
        String content,
        String authorName,
        boolean isMyPost,
        int commentCount,
        Instant createdAt,
        List<CommentResponseDto> comments
) {
}