package com.moau.moau.team.service;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberStatus;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.team.dto.response.TeamDetailResponse;
import com.moau.moau.team.dto.response.TeamResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamQueryService {

    private final TeamRepository teams;
    private final TeamMemberRepository teamMembers;

    /** 내가 소유한 팀 목록 */
    @Transactional(readOnly = true)
    public List<TeamResponse> getOwnedTeams(Long ownerUserId) {
        List<Team> list = teams.findByOwnerIdAndDeletedAtIsNull(ownerUserId);

        return list.stream()
                .map(TeamResponse::from)
                .toList();
    }

    /** 팀 상세 조회 (오너만 가능) */
    @Transactional(readOnly = true)
    public TeamDetailResponse getTeamDetail(Long currentUserId, Long teamId) {

        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() ->
                        new IllegalArgumentException(CommonError.TEAM_NOT_FOUND.getMessage())
                );

        if (!team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException(CommonError.TEAM_VIEW_FORBIDDEN.getMessage());
        }

        return TeamDetailResponse.from(team);
    }

    /** 내가 속한 팀 목록 (ACTIVE만) */
    @Transactional(readOnly = true)
    public List<TeamResponse> getMyTeams(Long userId) {

        List<TeamMember> memberships = teamMembers.findByUserId(userId);

        return memberships.stream()
                .filter(m -> m.getStatus() == TeamMemberStatus.ACTIVE)
                .map(TeamMember::getTeam)
                .filter(team -> !team.isDeleted())
                .distinct()
                .map(TeamResponse::from)
                .toList();
    }
}
