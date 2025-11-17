package com.moau.moau.schedule.service;

import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.global.util.SecurityUtil;
import com.moau.moau.schedule.domain.Schedule;
import com.moau.moau.schedule.dto.ScheduleCreateRequest;
import com.moau.moau.schedule.dto.ScheduleResponse;
import com.moau.moau.schedule.repository.ScheduleRepository;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    public List<ScheduleResponse> getTeamSchedules(Long teamId, int year, int month) {
        // 1. [추가] 현재 유저 ID를 가져옵니다.
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 2. 팀이 존재하는지 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        // 3. [핵심 로직 추가] 현재 유저가 해당 팀의 멤버인지 확인
        boolean isMember = teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, currentUserId);

        if (!isMember) {
            // 멤버가 아니면 403 Forbidden 예외를 발생시킵니다.
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        // 4. 시간 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        Instant startOfMonth = yearMonth.atDay(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Instant endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul")).toInstant();

        // 5. 일정 조회 (멤버십이 확인되었으므로 안전함)
        List<Schedule> schedules = scheduleRepository.findByTeam_IdAndStartsAtBetween(teamId, startOfMonth, endOfMonth);

        // 6. DTO 변환
        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createSchedule(Long teamId, ScheduleCreateRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // [추가된 보안 로직 시작] 현재 유저가 해당 팀의 멤버인지 확인
        boolean isMember = teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, currentUserId);
        if (!isMember) {
            // 멤버가 아니면 403 Forbidden 예외를 발생시킵니다.
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }
        // [추가된 보안 로직 끝]

        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + currentUserId));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        Schedule schedule = request.toEntity(team, creator);
        Schedule savedSchedule = scheduleRepository.save(schedule);

        return savedSchedule.getId();
    }

    public List<ScheduleResponse> getMySchedules(int year, int month) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 1. 내가 속한 모든 팀의 ID 목록을 조회합니다. (이 로직 자체가 보안 필터 역할을 함)
        List<TeamMember> myTeams = teamMemberRepository.findByUserId(currentUserId);
        List<Long> myTeamIds = myTeams.stream()
                .map(teamMember -> teamMember.getTeam().getId())
                .collect(Collectors.toList());

        // 2. 만약 속한 팀이 하나도 없으면, 빈 목록을 반환합니다.
        if (myTeamIds.isEmpty()) {
            return List.of();
        }

        // 3. 시간 계산 로직
        YearMonth yearMonth = YearMonth.of(year, month);
        Instant startOfMonth = yearMonth.atDay(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Instant endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul")).toInstant();

        // 4. 여러 팀 ID를 사용해 일정 조회
        List<Schedule> schedules = scheduleRepository.findByTeam_IdInAndStartsAtBetween(myTeamIds, startOfMonth, endOfMonth);

        // 5. DTO 변환
        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }
}