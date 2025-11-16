package com.moau.moau.schedule.controller;

import com.moau.moau.schedule.dto.ScheduleCreateRequest;
import com.moau.moau.schedule.dto.ScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "스케줄 API", description = "캘린저 조회 및 일정(반복 일정 포함) 관리 API")
public interface ScheduleControllerSwagger {

    @Operation(summary = "내 캘린더 조회 (통합)",
            description = "내가 속한 모든 팀의 일정을 포함한 '내 캘린더'를 월별로 조회합니다.")
    @Parameter(name = "year", description = "조회할 연도", example = "2025")
    @Parameter(name = "month", description = "조회할 월", example = "11")
    ResponseEntity<List<ScheduleResponse>> getMySchedules(
            @RequestParam Integer year,
            @RequestParam Integer month
    );

    @Operation(summary = "팀 캘린더 조회 (개별)",
            description = "특정 팀(모임)의 캘린더를 월별로 조회합니다.")
    @Parameter(name = "teamId", description = "조회할 팀 ID", example = "1")
    @Parameter(name = "year", description = "조회할 연도", example = "2025")
    @Parameter(name = "month", description = "조회할 월", example = "11")
    ResponseEntity<List<ScheduleResponse>> getTeamSchedules(
            @PathVariable Long teamId,
            @RequestParam Integer year,
            @RequestParam Integer month
    );

    @Operation(summary = "팀 일정 생성",
            description = "특정 팀에 새 일정을 생성합니다. (반복 일정 포함 가능)")
    @Parameter(name = "teamId", description = "일정을 생성할 팀 ID", example = "1")
    ResponseEntity<Long> createSchedule(
            @PathVariable Long teamId,
            @RequestBody ScheduleCreateRequest request
    );

    @Operation(summary = "단일 일정 수정",
            description = "특정 일정(scheduleId) 하나를 수정합니다.")
    @Parameter(name = "scheduleId", description = "수정할 일정 ID", example = "101")
    ResponseEntity<?> updateSchedule(
            @PathVariable Long scheduleId
    );

    @Operation(summary = "단일 일정 삭제",
            description = "특정 일정(scheduleId) 하나를 삭제합니다.")
    @Parameter(name = "scheduleId", description = "삭제할 일정 ID", example = "101")
    ResponseEntity<?> deleteSchedule(@PathVariable Long scheduleId);

    @Operation(summary = "반복 일정 전체 수정",
            description = "반복 ID(recurringId)가 동일한 모든 일정을 '전체' 수정합니다.")
    @Parameter(name = "recurringId", description = "수정할 반복 일정의 고유 ID", example = "recurr-abc-123")
    ResponseEntity<?> updateRecurringSchedules(
            @PathVariable String recurringId
    );

    @Operation(summary = "반복 일정 전체 삭제",
            description = "반복 ID(recurringId)가 동일한 모든 일정을 '전체' 삭제합니다.")
    @Parameter(name = "recurringId", description = "삭제할 반복 일정의 고유 ID", example = "recurr-abc-123")
    ResponseEntity<?> deleteRecurringSchedules(@PathVariable String recurringId);
}