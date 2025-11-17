// src/main/java/com/moau/moau/team/service/TeamMemberService.java
package com.moau.moau.team.service;

import com.moau.moau.team.domain.*;
import com.moau.moau.team.dto.response.TeamMemberResponse;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamRepository teams;
    private final TeamMemberRepository teamMembers;
    private final UserRepository users;

    /**
     * 팀 멤버 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamsMembers(Long teamId) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        return teamMembers.findAllByTeam(team)
                .stream()
                .filter(m -> m.getStatus() == TeamMemberStatus.ACTIVE) // ACTIVE만
                .map(TeamMemberResponse::from)
                .toList();
    }

    /**
     * 팀 생성 시 오너를 팀 멤버로 추가
     */
    @Transactional
    public void addOwnerMember(Long teamId, Long ownerUserId) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        User owner = users.findById(ownerUserId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        TeamMemberId id = new TeamMemberId(teamId, ownerUserId);

        if (teamMembers.existsById(id)) {
            return;
        }

        TeamMember member = TeamMemberFactory.create(
                team,
                owner,
                "OWNER",
                TeamMemberStatus.ACTIVE, // 오너는 ACTIVE
                ownerUserId
        );

        teamMembers.save(member);
    }

    /**
     * 팀 나가기 (일반 멤버 전용, 팀장은 나갈 수 없음)
     */
    @Transactional
    public void leaveTeam(Long currentUserId, Long teamId) {
        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        if (team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException("대표는 팀을 나갈 수 없습니다. 팀을 삭제하거나 팀장을 다른 멤버에게 양도해야 합니다.");
        }

        TeamMemberId id = new TeamMemberId(teamId, currentUserId);

        TeamMember member = teamMembers.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 팀의 멤버가 아닙니다."));

        if (member.getStatus() == TeamMemberStatus.LEFT) {
            throw new IllegalStateException("이미 팀을 나간 멤버입니다.");
        }

        member.setStatus(TeamMemberStatus.LEFT);
        member.setUpdatedBy(currentUserId);
    }
}
