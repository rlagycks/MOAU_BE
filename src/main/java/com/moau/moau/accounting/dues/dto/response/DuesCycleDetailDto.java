package com.moau.moau.accounting.dues.dto.response;

import java.time.LocalDate;
import java.util.List;

public record DuesCycleDetailDto(
        Long cycleId,
        String cycleName,
        LocalDate startDate,
        LocalDate endDate,

        List<DuesMemberDto> paidMembers,
        List<DuesMemberDto> unpaidMembers,

        int totalMemberCount,
        int paidCount,
        long totalExpectedAmount,
        long totalCollectedAmount
) {
}