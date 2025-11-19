// src/main/java/com/moau.moau.schedule.dto/ScheduleUpdateRequest.java
package com.moau.moau.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor // ⬅️ 이 생성자가 필수입니다.
@AllArgsConstructor
@Schema(description = "단일 일정 수정 요청 DTO")
public class ScheduleUpdateRequest {

    @NotBlank(message = "일정 제목은 필수 입력 항목입니다.")
    @Schema(description = "일정 제목", example = "팀 프로젝트 중간 발표")
    private String title;

    @Schema(description = "일정 상세 설명", example = "각자 맡은 파트 구현 현황 및 다음 스프린트 계획 논의")
    private String description;

    @Schema(description = "일정 장소", example = "온라인 (Zoom)")
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
}