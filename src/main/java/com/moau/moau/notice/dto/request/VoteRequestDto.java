package com.moau.moau.notice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record VoteRequestDto(
        @NotEmpty(message = "최소 하나 이상의 항목을 선택해야 합니다.")
        List<Long> pollOptionIds
) {
}