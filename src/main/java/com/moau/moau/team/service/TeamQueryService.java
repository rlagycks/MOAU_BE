package com.moau.moau.team.service;

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

    @Transactional(readOnly = true)
    public List<TeamResponse> getOwnedTeams(Long ownerUserId) {
        List<Team> list = teams.findByOwnerIdAndDeletedAtIsNull(ownerUserId);
        return list.stream()
                .map(TeamResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamDetailResponse getTeamDetail(Long currentUserId, Long teamId) {
        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (!team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException("팀 조회 권한이 없습니다.");
        }

        return TeamDetailResponse.from(team);
    }
    @Transactional(readOnly = true)
    public List<TeamResponse> getMyTeams(Long userId) {
        List<TeamMember> memberships = teamMembers.findByUserId(userId);

        return memberships.stream()
                .filter(m -> m.getStatus() == TeamMemberStatus.ACTIVE) // ACTIVE 멤버만
                .map(TeamMember::getTeam)
                .filter(team -> !team.isDeleted()) // BaseSoftDelete 사용
                .distinct()
                .map(TeamResponse::from)
                .toList();
    }

}

