package com.moau.moau.schedule.controller;

import com.moau.moau.schedule.dto.ScheduleCreateRequest;
import com.moau.moau.schedule.dto.ScheduleDetailResponse;
import com.moau.moau.schedule.dto.ScheduleResponse;
import com.moau.moau.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController implements ScheduleControllerSwagger{

    private final ScheduleService scheduleService;

    // 1. 내 캘린더 조회 (통합 뷰)
    @GetMapping("/schedules/me")
    public ResponseEntity<List<ScheduleResponse>> getMySchedules(@RequestParam Integer year,
                                                                 @RequestParam Integer month) {
        List<ScheduleResponse> schedules = scheduleService.getMySchedules(year, month);
        return ResponseEntity.ok(schedules);
    }

    // 2. 팀 캘린더 조회 (개별 뷰)
    @GetMapping("/teams/{teamId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> getTeamSchedules(@PathVariable Long teamId,
                                                                   @RequestParam Integer year,
                                                                   @RequestParam Integer month) {
        List<ScheduleResponse> schedules = scheduleService.getTeamSchedules(teamId, year, month);
        return ResponseEntity.ok(schedules);
    }

    // 3. 팀 일정 생성
    @PostMapping("/teams/{teamId}/schedules")
    public ResponseEntity<Long> createSchedule(@PathVariable Long teamId,
                                               @RequestBody ScheduleCreateRequest request) {
        Long scheduleId = scheduleService.createSchedule(teamId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleId);
    }

    // 4. 일정 상세 조회 (Read Single)
    @GetMapping("/schedules/{scheduleId}")
    public ResponseEntity<ScheduleDetailResponse> getScheduleDetail(@PathVariable Long scheduleId) {
        ScheduleDetailResponse response = scheduleService.getScheduleDetail(scheduleId);
        return ResponseEntity.ok(response);
    }

}