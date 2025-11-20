package com.moau.moau.accounting.dues.service;

import com.moau.moau.accounting.dues.domain.DuesCycle;
import com.moau.moau.accounting.dues.domain.DuesMemberStatus;
import com.moau.moau.accounting.dues.domain.DuesStatus;
import com.moau.moau.accounting.dues.dto.response.DuesCycleDetailDto;
import com.moau.moau.accounting.dues.dto.response.DuesCycleDto;
import com.moau.moau.accounting.dues.dto.response.DuesMemberDto;
import com.moau.moau.global.exception.error.DuesError;
import com.moau.moau.accounting.dues.repository.DuesCycleRepository;
import com.moau.moau.accounting.dues.repository.DuesMemberStatusRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.team.domain.DuesPeriod;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberStatus;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DuesQueryService {

    private final DuesCycleRepository duesCycleRepository;
    private final DuesMemberStatusRepository duesMemberStatusRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final DuesCycleCalculator calculator;

    // API 1: 회비 주기 목록 조회
    @Transactional(readOnly = true)
    public List<DuesCycleDto> getCycles(Long teamId) {
        return duesCycleRepository.findAllByTeamIdOrderByStartDateDesc(teamId)
                .stream()
                .map(DuesCycleDto::from)
                .toList();
    }

    // API 2: 특정 주기의 납부 현황 조회 (Lazy Loading 포함)
    // * 트랜잭션이 readOnly = false 여야 합니다 (생성 로직 때문)
    @Transactional
    public DuesCycleDetailDto getCycleStatus(Long teamId, LocalDate targetDate) {
        Team team = teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        if (team.getDuesPeriod() == DuesPeriod.NONE) {
            throw new BusinessException(DuesError.TEAM_SETTING_NOT_FOUND);
        }

        // 1. 타겟 날짜가 포함되는 주기 계산
        DuesCycleCalculator.CycleRange range = calculator.calculateCurrentRange(team.getDuesPeriod(), targetDate);

        // 2. DB 조회 -> 없으면 생성 (Lazy Loading)
        DuesCycle cycle = duesCycleRepository.findByTeamIdAndStartDate(teamId, range.start())
                .orElseGet(() -> createNewCycle(team, range));

        // 3. 상세 DTO 변환
        return createDetailDto(cycle);
    }

    // (Overloading) ID로 조회하는 경우
    @Transactional(readOnly = true)
    public DuesCycleDetailDto getCycleStatusById(Long teamId, Long cycleId) {
        DuesCycle cycle = duesCycleRepository.findByIdAndTeamId(cycleId, teamId)
                .orElseThrow(() -> new BusinessException(DuesError.CYCLE_NOT_FOUND));
        return createDetailDto(cycle);
    }

    // --- Private Helpers ---

    private DuesCycle createNewCycle(Team team, DuesCycleCalculator.CycleRange range) {
        // 1. 주기 생성
        DuesCycle cycle = DuesCycle.builder()
                .teamId(team.getId())
                .name(range.name())
                .startDate(range.start())
                .endDate(range.end())
                .build();
        duesCycleRepository.save(cycle);

        // 2. 현재 Active 멤버들에 대해 UNPAID 상태 생성
        List<TeamMember> members = teamMemberRepository.findAllByTeam(team);
        List<DuesMemberStatus> statuses = new ArrayList<>();

        for (TeamMember m : members) {
            if (m.getStatus() == TeamMemberStatus.ACTIVE) {
                statuses.add(DuesMemberStatus.builder()
                        .cycle(cycle)
                        .userId(m.getUser().getId())
                        .amount(team.getDuesAmount())
                        .build());
            }
        }
        duesMemberStatusRepository.saveAll(statuses);

        return cycle;
    }

    private DuesCycleDetailDto createDetailDto(DuesCycle cycle) {
        List<DuesMemberStatus> statusList = duesMemberStatusRepository.findAllByCycle(cycle);

        List<DuesMemberDto> paidMembers = new ArrayList<>();
        List<DuesMemberDto> unpaidMembers = new ArrayList<>();
        long totalCollected = 0;
        long totalExpected = 0;

        for (DuesMemberStatus status : statusList) {
            String userName = teamMemberRepository.findByTeamIdAndUserId(cycle.getTeamId(), status.getUserId())
                    .map(tm -> tm.getUser().getNickname())
                    .orElse("(알수없음)");

            DuesMemberDto dto = new DuesMemberDto(
                    status.getUserId(),
                    userName,
                    status.getStatus(),
                    status.getAmount(),
                    status.getPaidAt(),
                    null // memo
            );

            totalExpected += status.getAmount();

            if (status.getStatus() == DuesStatus.PAID) {
                paidMembers.add(dto);
                totalCollected += status.getAmount();
            } else {
                unpaidMembers.add(dto);
            }
        }

        return new DuesCycleDetailDto(
                cycle.getId(),
                cycle.getName(),
                cycle.getStartDate(),
                cycle.getEndDate(),
                paidMembers,
                unpaidMembers,
                statusList.size(),
                paidMembers.size(),
                totalExpected,
                totalCollected
        );
    }
}