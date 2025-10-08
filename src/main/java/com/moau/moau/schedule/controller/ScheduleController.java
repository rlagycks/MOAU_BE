package com.moau.moau.schedule.controller;

import com.moau.moau.schedule.dto.ScheduleResponse;
import com.moau.moau.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 1. 내 캘린더 조회 (통합 뷰)
    @GetMapping("/schedules/me")
    public ResponseEntity<?> getMySchedules(@RequestParam Integer year, @RequestParam Integer month) {
        // TODO: scheduleService.getMySchedules(year, month) 호출 로직 구현
        return ResponseEntity.ok("내 캘린더 조회 성공");
    }

    // 2. 팀 캘린더 조회 (개별 뷰)
    @GetMapping("/teams/{teamId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> getTeamSchedules(@PathVariable Long teamId,
                                                                   @RequestParam Integer year,
                                                                   @RequestParam Integer month) {
        // 다음 단계에서 Service의 메소드 이름도 getTeamSchedules로 변경할 예정입니다.
        List<ScheduleResponse> schedules = scheduleService.getTeamSchedules(teamId, year, month);
        return ResponseEntity.ok(schedules);
    }

    // 3. 팀 일정 생성
    @PostMapping("/teams/{teamId}/schedules")
    public ResponseEntity<?> createSchedule(@PathVariable Long teamId /*, @RequestBody ... */) {
        // TODO: Request Body DTO를 만들고, service.createSchedule() 호출 로직 구현
        return ResponseEntity.ok(teamId + "번 팀에 일정 생성 성공");
    }

    // 4. 단일 일정 수정
    @PutMapping("/schedules/{scheduleId}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long scheduleId /*, @RequestBody ... */) {
        // TODO: Request Body DTO를 만들고, service.updateSchedule() 호출 로직 구현
        return ResponseEntity.ok(scheduleId + "번 일정 수정 성공");
    }

    // 5. 단일 일정 삭제
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long scheduleId) {
        // TODO: scheduleService.deleteSchedule(scheduleId) 호출 로직 구현
        return ResponseEntity.ok(scheduleId + "번 일정 삭제 성공");
    }

    // 6. 반복 일정 전체 수정
    @PutMapping("/schedules/recurring/{recurringId}")
    public ResponseEntity<?> updateRecurringSchedules(@PathVariable String recurringId /*, @RequestBody ... */) {
        // TODO: Request Body DTO를 만들고, service.updateRecurringSchedules() 호출 로직 구현
        return ResponseEntity.ok(recurringId + " 반복 일정 전체 수정 성공");
    }

    // 7. 반복 일정 전체 삭제
    @DeleteMapping("/schedules/recurring/{recurringId}")
    public ResponseEntity<?> deleteRecurringSchedules(@PathVariable String recurringId) {
        // TODO: scheduleService.deleteRecurringSchedules(recurringId) 호출 로직 구현
        return ResponseEntity.ok(recurringId + " 반복 일정 전체 삭제 성공");
    }
}