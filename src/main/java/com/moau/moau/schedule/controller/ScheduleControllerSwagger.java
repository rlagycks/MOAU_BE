package com.moau.moau.schedule.controller;

import com.moau.moau.schedule.dto.ScheduleCreateRequest;
import com.moau.moau.schedule.dto.ScheduleDetailResponse;
import com.moau.moau.schedule.dto.ScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "스케줄 API", description = "캘린더 조회 및 일정(반복 일정 포함) 관리 API")
public interface ScheduleControllerSwagger {

    @Operation(summary = "내 캘린더 조회 (통합)",
            description = "내가 속한 모든 팀의 일정을 포함한 '내 캘린더'를 월별로 조회합니다.")
    @Parameter(name = "year", description = "조회할 연도", example = "2025")
    @Parameter(name = "month", description = "조회할 월", example = "10")
    ResponseEntity<List<ScheduleResponse>> getMySchedules(
            @RequestParam Integer year,
            @RequestParam Integer month
    );

    @Operation(summary = "팀 캘린더 조회 (개별)",
            description = "특정 팀(모임)의 캘린더를 월별로 조회합니다.")
    @Parameter(name = "teamId", description = "조회할 팀 ID", example = "1")
    @Parameter(name = "year", description = "조회할 연도", example = "2025")
    @Parameter(name = "month", description = "조회할 월", example = "10")
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

    @Operation(summary = "일정 상세 조회",
            description = "특정 일정(scheduleId)의 모든 상세 정보를 조회합니다.")
    @Parameter(name = "scheduleId", description = "조회할 일정 ID", example = "1002")
    ResponseEntity<ScheduleDetailResponse> getScheduleDetail(
            @PathVariable Long scheduleId
    );

}