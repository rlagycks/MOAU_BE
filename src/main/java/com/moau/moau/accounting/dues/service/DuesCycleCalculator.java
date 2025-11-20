package com.moau.moau.accounting.dues.service;

import com.moau.moau.global.exception.error.DuesError;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.team.domain.DuesPeriod;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DuesCycleCalculator {

    public CycleRange calculateCurrentRange(DuesPeriod period, LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        return switch (period) {
            case MONTHLY -> {
                // 예: 2025-11-01 ~ 2025-11-30
                LocalDate start = LocalDate.of(year, month, 1);
                yield new CycleRange(
                        String.format("%d년 %d월", year, month),
                        start,
                        start.plusMonths(1).minusDays(1)
                );
            }
            case QUARTERLY -> {
                // 예: 2025-10-01 ~ 2025-12-31 (4분기)
                int quarter = (month - 1) / 3 + 1;
                LocalDate start = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
                yield new CycleRange(
                        String.format("%d년 %d분기", year, quarter),
                        start,
                        start.plusMonths(3).minusDays(1)
                );
            }
            case HALF_YEARLY -> {
                int half = (month - 1) / 6 + 1;
                LocalDate start = LocalDate.of(year, (half - 1) * 6 + 1, 1);
                yield new CycleRange(
                        String.format("%d년 %s반기", year, (half == 1 ? "상" : "하")),
                        start,
                        start.plusMonths(6).minusDays(1)
                );
            }
            case YEARLY -> {
                LocalDate start = LocalDate.of(year, 1, 1);
                yield new CycleRange(
                        String.format("%d년", year),
                        start,
                        start.plusMonths(12).minusDays(1)
                );
            }
            case NONE -> throw new BusinessException(DuesError.TEAM_SETTING_NOT_FOUND, "회비 설정이 없는 팀입니다.");
        };
    }

    public record CycleRange(String name, LocalDate start, LocalDate end) {}
}