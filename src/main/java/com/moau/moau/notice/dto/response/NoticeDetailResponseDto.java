package com.moau.moau.notice.dto.response;

import java.time.Instant;
import java.util.List;

public record NoticeDetailResponseDto(
        Long noticeId,
        String title,
        String content,
        String authorName,
        boolean isPinned,
        boolean isMyNotice,
        Instant createdAt,
        List<String> imageUrls,

        PollDto poll
) {
}