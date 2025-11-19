// src/main/java/com/moau/moau/schedule/dto/ScheduleDetailResponse.java
package com.moau.moau.schedule.dto;

import com.moau.moau.schedule.domain.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ScheduleDetailResponse {

    private Long scheduleId;
    private Long teamId;

    private String title;
    private String description; // ⬅️ 상세 정보
    private String location;    // ⬅️ 상세 정보

    private Instant startsAt;
    private Instant endsAt;

    private String recurringId; // ⬅️ 반복 일정 ID

    // 일정 생성자 정보 (별도 조인 필요)
    private Long creatorId;
    private String creatorNickname;

    // DTO 변환을 위한 팩토리 메서드
    public static ScheduleDetailResponse from(Schedule schedule) {
        // Schedule 엔티티가 Team과 Creator(User)를 참조하고 있다고 가정합니다.
        return ScheduleDetailResponse.builder()
                .scheduleId(schedule.getId())
                .teamId(schedule.getTeam().getId())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .location(schedule.getLocation())
                .startsAt(schedule.getStartsAt())
                .endsAt(schedule.getEndsAt())
                .recurringId(schedule.getRecurringId())
                .creatorId(schedule.getCreator().getId())
                .creatorNickname(schedule.getCreator().getNickname())
                .build();
    }
}