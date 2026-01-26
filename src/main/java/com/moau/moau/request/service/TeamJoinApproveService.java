// src/main/java/com/moau/moau/request/service/TeamJoinApproveService.java
package com.moau.moau.request.service;

import com.moau.moau.global.exception.BusinessException;
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
    public void approve(Long teamId, Long requestId) {

        // 1) 승인자 권한 검증
        User approver = requireAdminPermission(teamId);

        // 2) JoinRequest 조회
        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new BusinessException(CommonError.JOIN_REQUEST_NOT_FOUND));

        Team team = req.getTeam();
        User targetUser = req.getRequestUser();

        if (!team.getId().equals(teamId) || team.isDeleted()) {
            throw new BusinessException(CommonError.TEAM_NOT_FOUND);
        }

        // 3) 이미 처리된 요청인지 검사
        if (!JoinRequestStatus.PENDING.equals(req.getStatus())) {
            throw new BusinessException(CommonError.JOIN_REQUEST_ALREADY_HANDLED);
        }

        // 4) 이미 팀 멤버인지 검사
        if (teamMembers.existsByTeamAndUser(team, targetUser)) {
            throw new BusinessException(CommonError.TEAM_MEMBER_ALREADY_EXISTS);
        }

        // 5) 팀 멤버 등록
        TeamMember member = TeamMemberFactory.create(
                team,
                targetUser,
                TeamMemberRole.MEMBER,
                TeamMemberStatus.ACTIVE,
                approver.getId()
        );
        teamMembers.save(member);

        // 6) JoinRequest 승인 처리
        JoinRequestFactory.approve(req, approver, Instant.now());
    }

    /** 가입 거절 */
    @Transactional
    public void reject(Long teamId, Long requestId) {

        // 1) 승인자 권한 검증
        User approver = requireAdminPermission(teamId);

        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new BusinessException(CommonError.JOIN_REQUEST_NOT_FOUND));

        if (!req.getTeam().getId().equals(teamId) || req.getTeam().isDeleted()) {
            throw new BusinessException(CommonError.TEAM_NOT_FOUND);
        }

        // 이미 처리된 요청인지
        if (!JoinRequestStatus.PENDING.equals(req.getStatus())) {
            throw new BusinessException(CommonError.JOIN_REQUEST_ALREADY_HANDLED);
        }

        // 거절 처리
        JoinRequestFactory.reject(req, approver, Instant.now());
    }

    /**
     * Admin 권한을 가진 사용자인지 검증하고 User 객체를 반환합니다.
     *
     * @param teamId 팀 ID
     * @return 검증된 User 객체
     * @throws BusinessException 사용자가 없거나 권한이 없는 경우
     */
    private User requireAdminPermission(Long teamId) {
        Long approverUserId = SecurityUtil.getCurrentUserId();
        User approver = users.findById(approverUserId)
                .orElseThrow(() -> new BusinessException(CommonError.USER_NOT_FOUND));

        var approverMember = teamMembers.findActiveMember(teamId, approverUserId)
                .orElseThrow(() -> new BusinessException(CommonError.ACCESS_DENIED));
        if (!approverMember.getRole().isAtLeast(TeamMemberRole.ADMIN)) {
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        return approver;
    }
}
