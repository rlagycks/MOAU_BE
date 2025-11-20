package com.moau.moau.accounting.dues.dto.response;

import com.moau.moau.accounting.dues.domain.DuesStatus;
import java.time.Instant;

public record DuesMemberDto(
        Long userId,
        String name,
        DuesStatus status,
        Long amount,
        Instant paidAt,
        String memo
) {
}