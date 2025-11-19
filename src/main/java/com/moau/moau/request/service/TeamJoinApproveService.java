// src/main/java/com/moau/moau/request/service/TeamJoinApproveService.java
package com.moau.moau.request.service;

import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.global.exception.error.CommonError;
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

    /** 가입 승인 */
    @Transactional
    public void approve(Long requestId) {

        // 1) 승인자 조회
        Long approverUserId = SecurityUtil.getCurrentUserId();
        User approver = users.findById(approverUserId)
                .orElseThrow(() -> new IllegalStateException(CommonError.USER_NOT_FOUND.getMessage()));

        // 2) JoinRequest 조회
        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new IllegalStateException(CommonError.JOIN_REQUEST_NOT_FOUND.getMessage()));

        Team team = req.getTeam();
        User targetUser = req.getRequestUser();

        // 3) 이미 처리된 요청인지 검사
        if (!JoinRequestStatus.PENDING.equals(req.getStatus())) {
            throw new IllegalStateException(CommonError.JOIN_REQUEST_ALREADY_HANDLED.getMessage());
        }

        // 4) 이미 팀 멤버인지 검사
        if (teamMembers.existsByTeamAndUser(team, targetUser)) {
            throw new IllegalStateException(CommonError.TEAM_MEMBER_ALREADY_EXISTS.getMessage());
        }

        // 5) 팀 멤버 등록
        TeamMember member = TeamMemberFactory.create(
                team,
                targetUser,
                TeamMemberRole.MEMBER,
                TeamMemberStatus.ACTIVE,
                approverUserId
        );
        teamMembers.save(member);

        // 6) JoinRequest 승인 처리
        JoinRequestFactory.approve(req, approver, Instant.now());
    }

    /** 가입 거절 */
    @Transactional
    public void reject(Long requestId) {

        Long approverUserId = SecurityUtil.getCurrentUserId();
        User approver = users.findById(approverUserId)
                .orElseThrow(() -> new IllegalStateException(CommonError.USER_NOT_FOUND.getMessage()));

        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new IllegalStateException(CommonError.JOIN_REQUEST_NOT_FOUND.getMessage()));

        // 이미 처리된 요청인지
        if (!JoinRequestStatus.PENDING.equals(req.getStatus())) {
            throw new IllegalStateException(CommonError.JOIN_REQUEST_ALREADY_HANDLED.getMessage());
        }

        // 거절 처리
        JoinRequestFactory.reject(req, approver, Instant.now());
    }
}
