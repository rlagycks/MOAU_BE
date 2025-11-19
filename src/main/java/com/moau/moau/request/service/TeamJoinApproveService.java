// src/main/java/com/moau/moau/request/service/TeamJoinApproveService.java
package com.moau.moau.request.service;

import com.moau.moau.request.domain.JoinRequest;
import com.moau.moau.request.domain.JoinRequestFactory;
import com.moau.moau.request.domain.JoinRequestStatus;
import com.moau.moau.request.repository.JoinRequestRepository;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberFactory;
import com.moau.moau.team.domain.TeamMemberRole;
import com.moau.moau.team.domain.TeamMemberStatus;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.service.TeamAuthorizationService;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TeamJoinApproveService {

    private final UserRepository users;
    private final JoinRequestRepository joinRequests;
    private final TeamMemberRepository teamMembers;
    private final TeamAuthorizationService teamAuth;

    /**
     * [ADMIN + OWNER] 가입 승인 + TeamMember 생성
     */
    @Transactional
    public void approve(Long requestId, Long approverUserId) {
        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        Team team = req.getTeam();
        User targetUser = req.getRequestUser();
        Long teamId = team.getId();

        // ADMIN 또는 OWNER 권한 체크
        TeamMember approverMember = teamAuth.requireAdminOrOwner(approverUserId, teamId);
        User approver = approverMember.getUser();

        if (!JoinRequestStatus.PENDING.equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 가입 신청입니다.");
        }

        if (teamMembers.existsByTeamAndUser(team, targetUser)) {
            throw new IllegalStateException("이미 이 팀의 멤버입니다.");
        }

        // TeamMember 생성 (ACTIVE, 기본 MEMBER)
        TeamMember member = TeamMemberFactory.create(
                team,
                targetUser,
                TeamMemberRole.MEMBER,
                TeamMemberStatus.ACTIVE,
                approverUserId
        );
        teamMembers.save(member);

        // JoinRequest 상태 APPROVED 로 변경
        JoinRequestFactory.approve(req, approver, Instant.now());
    }

    /**
     * [ADMIN + OWNER] 가입 거절
     */
    @Transactional
    public void reject(Long requestId, Long approverUserId) {
        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        Team team = req.getTeam();
        Long teamId = team.getId();

        // ADMIN 또는 OWNER 권한 체크
        TeamMember approverMember = teamAuth.requireAdminOrOwner(approverUserId, teamId);
        User approver = approverMember.getUser();

        if (JoinRequestStatus.PENDING.equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 가입 신청입니다.");
        }

        JoinRequestFactory.reject(req, approver, Instant.now());
    }
}
