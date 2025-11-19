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

    public List<ScheduleResponse> getTeamSchedules(Long teamId, int year, int month) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 2. 팀이 존재하는지 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        // 3. [핵심 로직 추가] 현재 유저가 해당 팀의 멤버인지 확인
        boolean isMember = teamMemberRepository.existsByTeam_IdAndUser_IdAndStatus(
                teamId,
                currentUserId,
                TeamMemberStatus.ACTIVE
        );

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

    // 일정 상세 조회 (Read Single)
    public ScheduleDetailResponse getScheduleDetail(Long scheduleId) {
        // 1. 일정 존재 확인 (404 Not Found)
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(CommonError.SCHEDULE_NOT_FOUND));

        // 2. 일정의 teamId를 가져와 멤버십 확인 (보안 체크 - 403 Forbidden)
        Long teamId = schedule.getTeam().getId();
        Long currentUserId = SecurityUtil.getCurrentUserId();

        boolean isMember = teamMemberRepository.existsByTeam_IdAndUser_IdAndStatus(
                teamId,
                currentUserId,
                TeamMemberStatus.ACTIVE
        );

        if (!isMember) {
            // 멤버가 아니면 403 Forbidden 예외를 발생시킵니다.
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        // 3. DTO로 변환 후 반환
        return ScheduleDetailResponse.from(schedule);
    }

    // [추가] 단일 일정 수정 로직
    @Transactional
    public Long updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {

        // 1. 일정 존재 확인 (404 Not Found)
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(CommonError.SCHEDULE_NOT_FOUND));

        // 2. 일정의 teamId를 가져와 멤버십 확인 (보안 체크 - 403 Forbidden)
        Long teamId = schedule.getTeam().getId();
        Long currentUserId = SecurityUtil.getCurrentUserId();

        boolean isMember = teamMemberRepository.existsByTeam_IdAndUser_IdAndStatus(
                teamId,
                currentUserId,
                TeamMemberStatus.ACTIVE
        );

        if (!isMember) {
            // 멤버가 아니면 403 Forbidden 예외를 발생시킵니다.
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        // 3. 엔티티 내용 수정 (Dirty Checking)
        // TODO: Schedule 엔티티에 update() 메서드가 필요합니다.
        schedule.update(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getStartsAt(),
                request.getEndsAt(),
                request.isAllDay()
        );

        // 4. (save 없이) ID 반환 - @Transactional에 의해 자동 반영됨
        return schedule.getId();
    }

    // [추가] 단일 일정 삭제 로직
    @Transactional
    public void deleteSchedule(Long scheduleId) {

        // 1. 일정 존재 확인 (404 Not Found)
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(CommonError.SCHEDULE_NOT_FOUND));

        // 2. 일정의 teamId를 가져와 멤버십 확인 (보안 체크 - 403 Forbidden)
        Long teamId = schedule.getTeam().getId();
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 현재 유저가 해당 일정의 팀 멤버(ACTIVE)인지 확인합니다.
        boolean isMember = teamMemberRepository.existsByTeam_IdAndUser_IdAndStatus(
                teamId,
                currentUserId,
                TeamMemberStatus.ACTIVE
        );

        if (!isMember) {
            // 팀 멤버가 아니면 403 Forbidden 예외를 발생시킵니다.
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        // 3. 삭제 실행
        scheduleRepository.delete(schedule);
    }

    @Transactional
    public Long createSchedule(Long teamId, ScheduleCreateRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // [추가된 보안 로직 시작] 현재 유저가 해당 팀의 멤버인지 확인
        boolean isMember = teamMemberRepository.existsByTeam_IdAndUser_IdAndStatus(
                teamId,
                currentUserId,
                TeamMemberStatus.ACTIVE
        );

        if (!isMember) {
            // 멤버가 아니면 403 Forbidden 예외를 발생시킵니다.
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

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