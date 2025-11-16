// src/main/java/com/moau/moau/team/service/TeamJoinApproveService.java
package com.moau.moau.team.service;

import com.moau.moau.team.domain.JoinRequest;
import com.moau.moau.team.domain.JoinRequestFactory;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberFactory;
import com.moau.moau.team.repository.JoinRequestRepository;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TeamJoinApproveService {

    private final TeamRepository teams;
    private final UserRepository users;
    private final JoinRequestRepository joinRequests;
    private final TeamMemberRepository teamMembers;

    @Transactional
    public void approve(Long teamId, Long requestId, Long approverUserId) {
        // 그룹 조회
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));

        // 승인자 조회
        User approver = users.findById(approverUserId)
                .orElseThrow(() -> new IllegalArgumentException("승인 사용자를 찾을 수 없습니다."));

        // 지금은 그룹장만 승인 가능 (권한 기능은 나중에 확장)
        if (!team.getOwner().getId().equals(approverUserId)) {
            throw new IllegalStateException("그룹장만 가입 신청을 승인할 수 있습니다.");
        }

        // 가입 신청 조회
        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        // 그룹 일치 여부 검증
        if (!req.getTeam().getId().equals(teamId)) {
            throw new IllegalStateException("그룹과 가입 신청 정보가 일치하지 않습니다.");
        }

        // PENDING만 승인 가능
        if (!"PENDING".equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 가입 신청입니다.");
        }

        User targetUser = req.getRequestUser();

        // 이미 멤버인 경우 방어
        if (teamMembers.existsByTeamAndUser(team, targetUser)) {
            throw new IllegalStateException("이미 이 그룹의 멤버입니다.");
        }

        // TeamMember 생성 (ACTIVE, 기본 MEMBER)
        TeamMember member = TeamMemberFactory.create(
                team,
                targetUser,
                "MEMBER",
                "ACTIVE",
                approverUserId
        );
        teamMembers.save(member);

        // JoinRequest 상태 APPROVED로 변경
        JoinRequestFactory.approve(req, approver, Instant.now());
    }
}
