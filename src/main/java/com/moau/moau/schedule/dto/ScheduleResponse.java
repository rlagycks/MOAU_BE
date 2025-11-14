package com.moau.moau.schedule.dto;

import com.moau.moau.schedule.domain.Schedule;
import java.time.Instant;

// Java 14부터 도입된 record는 Getter, 생성자, toString 등을 자동으로 만들어주는 편리한 클래스입니다.
public record ScheduleResponse(
        Long scheduleId,
        String title,
        Instant startsAt,
        Instant endsAt,
        String location
) {
    // Schedule 엔티티를 ScheduleResponse DTO로 변환해주는 정적 팩토리 메소드
    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getStartsAt(),
                schedule.getEndsAt(),
                schedule.getLocation()
        );
    }
}