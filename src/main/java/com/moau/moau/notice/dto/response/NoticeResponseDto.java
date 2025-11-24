package com.moau.moau.notice.dto.response;

import java.time.Instant;

public record NoticeResponseDto(
        Long noticeId,
        String title,
        String contentPreview, // (내용 50자 요약)
        String authorName,
        boolean isPinned,
        boolean hasPoll,       // (리스트에서 투표 아이콘 표시용)
        Instant createdAt
) {
}