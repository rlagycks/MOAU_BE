package com.moau.moau.schedule.service;

import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.schedule.domain.Schedule;
import com.moau.moau.schedule.dto.ScheduleResponse;
import com.moau.moau.schedule.repository.ScheduleRepository;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.repository.TeamRepository;
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
    private final TeamRepository teamRepository;

    public List<ScheduleResponse> getTeamSchedules(Long teamId, int year, int month) {
        // 팀이 존재하는지 확인하고, 없으면 BusinessException 발생
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        // 시간 계산 로직
        YearMonth yearMonth = YearMonth.of(year, month);
        Instant startOfMonth = yearMonth.atDay(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Instant endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul")).toInstant();

        // 일정 조회 로직
        List<Schedule> schedules = scheduleRepository.findByTeam_IdAndStartsAtBetween(teamId, startOfMonth, endOfMonth);

        // DTO 변환 로직
        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }
}