// src/main/java/com/moau/moau/team/service/TeamMemberService.java
package com.moau.moau.team.service;

import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberFactory;
import com.moau.moau.team.domain.TeamMemberId;
import com.moau.moau.team.dto.response.TeamMemberResponse;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamRepository teams;
    private final TeamMemberRepository teamMembers;
    private final UserRepository users;

    /**
     * 그룹 멤버 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getGroupMembers(Long teamId) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 그룹입니다."));

        return teamMembers.findAllByTeam(team)
                .stream()
                .map(TeamMemberResponse::from)
                .toList();
    }

    /**
     * 팀 생성 시 오너를 그룹 멤버로 추가
     */
    @Transactional
    public void addOwnerMember(Long teamId, Long ownerUserId) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 그룹입니다."));

        User owner = users.findById(ownerUserId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        TeamMemberId id = new TeamMemberId(teamId, ownerUserId);

        // 이미 멤버이면 아무 작업도 하지 않음
        if (teamMembers.existsById(id)) {
            return;
        }
        TeamMember member = TeamMemberFactory.create(
                team,
                owner,
                "OWNER",      // 역할
                "ACTIVE",     // 상태
                ownerUserId   // updatedBy
        );

        teamMembers.save(member);
    }
}
