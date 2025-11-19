package com.moau.moau.schedule.service;

import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.global.util.SecurityUtil;
import com.moau.moau.schedule.domain.Schedule;
import com.moau.moau.schedule.dto.ScheduleCreateRequest;
import com.moau.moau.schedule.dto.ScheduleDetailResponse;
import com.moau.moau.schedule.dto.ScheduleResponse;
import com.moau.moau.schedule.dto.ScheduleUpdateRequest;
import com.moau.moau.schedule.repository.ScheduleRepository;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberRole;
import com.moau.moau.team.domain.TeamMemberStatus;
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

    // 1. 팀 캘린더 조회
    public List<ScheduleResponse> getTeamSchedules(Long teamId, int year, int month) {
        if (!teamRepository.existsById(teamId)) {
            throw new BusinessException(CommonError.TEAM_NOT_FOUND);
        }

        // [안전장치 추가] 조회도 멤버여야 가능하므로 검증 추가 (선택사항이지만 권장)
        validateActiveMember(teamId);

        YearMonth yearMonth = YearMonth.of(year, month);
        Instant startOfMonth = yearMonth.atDay(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Instant endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul")).toInstant();

        List<Schedule> schedules = scheduleRepository.findByTeam_IdAndStartsAtBetween(teamId, startOfMonth, endOfMonth);

        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    // 2. 일정 상세 조회
    public ScheduleDetailResponse getScheduleDetail(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(CommonError.SCHEDULE_NOT_FOUND));

        validateActiveMember(schedule.getTeam().getId());

        return ScheduleDetailResponse.from(schedule);
    }

    // 3. 단일 일정 수정
    @Transactional
    public Long updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(CommonError.SCHEDULE_NOT_FOUND));

        validateAdminOrOwner(schedule.getTeam().getId());

        schedule.update(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getStartsAt(),
                request.getEndsAt(),
                request.isAllDay()
        );

        return schedule.getId();
    }

    // 4. 단일 일정 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(CommonError.SCHEDULE_NOT_FOUND));

        validateAdminOrOwner(schedule.getTeam().getId());

        scheduleRepository.delete(schedule);
    }

    // 5. 팀 일정 생성 (🚨 보안 로직 복구됨)
    @Transactional
    public Long createSchedule(Long teamId, ScheduleCreateRequest request) {
        // [✅ 수정됨] 서비스 계층에서도 관리자 권한을 명시적으로 검사합니다.
        // 어노테이션이 실패하더라도 여기서 막힙니다.
        validateAdminOrOwner(teamId);

        Long currentUserId = SecurityUtil.getCurrentUserId();

        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + currentUserId));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        Schedule schedule = request.toEntity(team, creator);
        Schedule savedSchedule = scheduleRepository.save(schedule);

        return savedSchedule.getId();
    }

    // 6. 내 캘린더 조회
    public List<ScheduleResponse> getMySchedules(int year, int month) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        List<TeamMember> myTeams = teamMemberRepository.findByUserId(currentUserId);
        List<Long> myTeamIds = myTeams.stream()
                .map(teamMember -> teamMember.getTeam().getId())
                .collect(Collectors.toList());

        if (myTeamIds.isEmpty()) {
            return List.of();
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        Instant startOfMonth = yearMonth.atDay(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant();
        Instant endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.of("Asia/Seoul")).toInstant();

        List<Schedule> schedules = scheduleRepository.findByTeam_IdInAndStartsAtBetween(myTeamIds, startOfMonth, endOfMonth);

        return schedules.stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    // 🔒 [내부 메서드 1] 단순 멤버십 확인
    private void validateActiveMember(Long teamId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        boolean isMember = teamMemberRepository.existsByTeam_IdAndUser_IdAndStatus(
                teamId,
                currentUserId,
                TeamMemberStatus.ACTIVE
        );

        if (!isMember) {
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }
    }

    // 🔒 [내부 메서드 2] 관리자/오너 권한 확인
    private void validateAdminOrOwner(Long teamId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, currentUserId)
                .orElseThrow(() -> new BusinessException(CommonError.ACCESS_DENIED));

        if (member.getStatus() != TeamMemberStatus.ACTIVE) {
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        if (!member.getRole().isAtLeast(TeamMemberRole.ADMIN)) {
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }
    }
}