package com.moau.moau.schedule.service;

import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
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

    @Transactional // 데이터를 저장하므로 클래스 레벨의 readOnly 설정을 덮어씁니다.
    public Long createSchedule(Long teamId, ScheduleCreateRequest request) {
        // TODO: 로그인 기능 구현 후 실제 인증된 유저 정보를 가져와야 합니다.
        // 지금은 임시로 DB에 있는 1번 유저를 생성자로 사용합니다.
        User creator = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=1"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        Schedule schedule = request.toEntity(team, creator);
        Schedule savedSchedule = scheduleRepository.save(schedule);

        return savedSchedule.getId();
    }

    public List<ScheduleResponse> getMySchedules(int year, int month) {
        // TODO: 로그인 기능 구현 후 실제 인증된 유저 정보를 가져와야 합니다.
        // 테스트를 위해 '멀티팀유저'의 ID인 2L로 변경합니다.
        Long currentUserId = 2L; // [✅ 1L -> 2L로 수정]

        // 1. 내가 속한 모든 팀의 ID 목록을 조회합니다.
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

        // 4. 여러 팀 ID를 사용해 일정 조회 로직
        List<Schedule> schedules = scheduleRepository.findByTeam_IdInAndStartsAtBetween(myTeamIds, startOfMonth, endOfMonth);

        // 5. DTO 변환 로직
        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }
}