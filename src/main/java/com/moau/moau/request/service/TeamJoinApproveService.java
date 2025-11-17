// src/main/java/com/moau/moau/team/service/TeamJoinApproveService.java
package com.moau.moau.request.service;

import com.moau.moau.request.domain.JoinRequest;
import com.moau.moau.request.domain.JoinRequestFactory;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberFactory;
import com.moau.moau.request.repository.JoinRequestRepository;
import com.moau.moau.team.domain.TeamMemberStatus;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TeamJoinApproveService {

    // TeamRepository 는 더 이상 안 써서 지워도 됨
    private final UserRepository users;
    private final JoinRequestRepository joinRequests;
    private final TeamMemberRepository teamMembers;

    @Transactional
    public void approve(Long requestId, Long approverUserId) {
        // 가입 신청 조회
        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        // 가입 신청에 연결된 그룹/유저 정보
        Team team = req.getTeam();
        User targetUser = req.getRequestUser();

        // 승인자 조회
        User approver = users.findById(approverUserId)
                .orElseThrow(() -> new IllegalArgumentException("승인 사용자를 찾을 수 없습니다."));

        // 지금은 그룹장만 승인 가능
        if (!team.getOwner().getId().equals(approverUserId)) {
            throw new IllegalStateException("그룹장만 가입 신청을 승인할 수 있습니다.");
        }

        // PENDING만 승인 가능
        if (!"PENDING".equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 가입 신청입니다.");
        }

        // 이미 멤버인 경우 방어
        if (teamMembers.existsByTeamAndUser(team, targetUser)) {
            throw new IllegalStateException("이미 이 그룹의 멤버입니다.");
        }

        // TeamMember 생성 (ACTIVE, 기본 MEMBER)
        TeamMember member = TeamMemberFactory.create(
                team,
                targetUser,
                "MEMBER",
                TeamMemberStatus.ACTIVE,
                approverUserId
        );
        teamMembers.save(member);

        // JoinRequest 상태 APPROVED 로 변경
        JoinRequestFactory.approve(req, approver, Instant.now());
    }
}
