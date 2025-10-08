package com.moau.moau.schedule.service;

import com.moau.moau.schedule.domain.Schedule;
import com.moau.moau.schedule.dto.ScheduleResponse;
import com.moau.moau.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    // [✅ 수정됨]
    public List<ScheduleResponse> getTeamSchedules(Long teamId, int year, int month) {
        // 1. 조회할 월의 시작일과 종료일을 계산합니다. (한국 시간 기준)
        YearMonth yearMonth = YearMonth.of(year, month);
        Instant startOfMonth = yearMonth.atDay(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Instant endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul")).toInstant();

        // 2. Repository를 호출하여 DB에서 Schedule 엔티티 목록을 가져옵니다.
        List<Schedule> schedules = scheduleRepository.findByTeam_IdAndStartsAtBetween(teamId, startOfMonth, endOfMonth);

        // 3. 가져온 엔티티 목록을 DTO 목록으로 변환합니다. (Java Stream API 사용)
        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    // TODO: 내 캘린더 조회 로직
    // TODO: 일정 생성 로직 (@Transactional 필요)
    // TODO: 단일 일정 수정/삭제 로직 (@Transactional 필요)
    // TODO: 반복 일정 전체 수정/삭제 로직 (@Transactional 필요)
}