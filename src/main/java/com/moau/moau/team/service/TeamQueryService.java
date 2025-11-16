package com.moau.moau.team.service;

import com.moau.moau.team.domain.Team;
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

    @Transactional(readOnly = true)
    public List<TeamResponse> getOwnedTeams(Long ownerUserId) {
        List<Team> list = teams.findByOwnerId(ownerUserId);
        return list.stream()
                .map(TeamResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamDetailResponse getTeamDetail(Long currentUserId, Long teamId) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (!team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException("팀 조회 권한이 없습니다.");
        }

        return TeamDetailResponse.from(team);
    }
}
