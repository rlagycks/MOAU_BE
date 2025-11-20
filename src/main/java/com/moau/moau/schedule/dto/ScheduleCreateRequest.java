package com.moau.moau.schedule.dto;

import com.moau.moau.schedule.domain.Schedule;
import com.moau.moau.team.domain.Team;
import com.moau.moau.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "팀 일정 생성 요청 DTO")
public class ScheduleCreateRequest {

    @NotBlank(message = "일정 제목은 필수 입력 항목입니다.")
    @Schema(description = "일정 제목", example = "팀 프로젝트 킥오프")
    private String title;

    @Schema(description = "일정 상세 설명", example = "프로젝트 목표 및 역할 분담 논의")
    private String description;

    @Schema(description = "일정 장소", example = "대회의실 A")
    private String location;

    @NotNull(message = "시작 시간은 필수 입력 항목입니다.")
    @Schema(description = "일정 시작 시간 (UTC 기준 Instant)", example = "2025-10-25T13:00:00Z")
    private Instant startsAt;

    @NotNull(message = "종료 시간은 필수 입력 항목입니다.")
    @Future(message = "종료 시간은 현재 시간 이후여야 합니다.")
    @Schema(description = "일정 종료 시간 (UTC 기준 Instant)", example = "2025-10-25T14:30:00Z")
    private Instant endsAt;

    @Schema(description = "종일 일정 여부", example = "false")
    private boolean isAllDay;

    // 엔티티 변환 메서드
    public Schedule toEntity(Team team, User creator) {
        return Schedule.builder()
                .team(team)
                .creator(creator)
                .title(this.title)
                .description(this.description)
                .location(this.location)
                .startsAt(this.startsAt)
                .endsAt(this.endsAt)
                .isAllDay(this.isAllDay)
                .build();
    }
}