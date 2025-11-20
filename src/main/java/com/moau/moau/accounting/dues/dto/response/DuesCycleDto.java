package com.moau.moau.accounting.dues.dto.response;

import com.moau.moau.accounting.dues.domain.DuesCycle;

import java.time.LocalDate;

public record DuesCycleDto(
        Long cycleId,
        String name,
        LocalDate startDate,
        LocalDate endDate
) {
    public static DuesCycleDto from(DuesCycle cycle) {
        return new DuesCycleDto(
                cycle.getId(),
                cycle.getName(),
                cycle.getStartDate(),
                cycle.getEndDate()
        );
    }
}