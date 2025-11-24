package com.moau.moau.notice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record NoticeCreateRequestDto(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100)
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        String content,

        boolean isPinned, // 상단 고정 여부

        @Size(max = 5, message = "이미지는 최대 5장까지 첨부할 수 있습니다.")
        List<String> imageKeys,

        @Valid
        PollCreateDto poll
) {
    // (내부 DTO) 투표 생성 정보
    public record PollCreateDto(
            @NotBlank(message = "투표 제목은 필수입니다.")
            String title,

            boolean allowMultiple,
            boolean isAnonymous,
            LocalDate deadline,

            @Size(min = 2, message = "투표 항목은 최소 2개 이상이어야 합니다.")
            List<String> options   // 예: ["참여", "불참"]
    ) {}
}