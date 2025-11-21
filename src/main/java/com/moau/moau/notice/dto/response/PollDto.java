package com.moau.moau.notice.dto.response;

import java.time.LocalDate;
import java.util.List;

public record PollDto(
        Long pollId,
        String title,
        boolean allowMultiple,
        boolean isAnonymous,
        boolean isClosed,      // 마감 여부
        LocalDate deadline,

        List<PollOptionDto> options, // 하위 옵션 리스트

        boolean isVoted,       // (내가) 투표 참여 여부
        int totalVoteCount     // 총 투표 참여자 수
) {
    // [요청하신 부분] 내부 레코드로 정의
    public record PollOptionDto(
            Long optionId,
            String text,
            int voteCount,     // 현재 득표수
            boolean isSelected // (내가) 이 항목을 선택했는지 여부
    ) {}
}