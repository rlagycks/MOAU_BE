package com.moau.moau.request.service;

import com.moau.moau.global.security.SecurityUtil;
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

    @Transactional
    public void approve(Long requestId) { // [수정] approverUserId 파라미터 제거
        Long approverUserId = SecurityUtil.getCurrentUserId(); // [추가]
        User approver = users.findById(approverUserId) // [추가] User 객체 조회
                .orElseThrow(() -> new IllegalArgumentException("승인자 정보를 찾을 수 없습니다."));

        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        Team team = req.getTeam();
        User targetUser = req.getRequestUser();

        if (!JoinRequestStatus.PENDING.equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 가입 신청입니다.");
        }

        if (teamMembers.existsByTeamAndUser(team, targetUser)) {
            throw new IllegalStateException("이미 이 팀의 멤버입니다.");
        }

        TeamMember member = TeamMemberFactory.create(
                team,
                targetUser,
                TeamMemberRole.MEMBER,
                TeamMemberStatus.ACTIVE,
                approverUserId
        );
        teamMembers.save(member);

        JoinRequestFactory.approve(req, approver, Instant.now());
    }

    @Transactional
    public void reject(Long requestId) { // [수정] approverUserId 파라미터 제거
        Long approverUserId = SecurityUtil.getCurrentUserId(); // [추가]
        User approver = users.findById(approverUserId)
                .orElseThrow(() -> new IllegalArgumentException("승인자 정보를 찾을 수 없습니다."));

        JoinRequest req = joinRequests.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        if (!JoinRequestStatus.PENDING.equals(req.getStatus())) {
            throw new IllegalStateException("이미 처리된 가입 신청입니다.");
        }

        JoinRequestFactory.reject(req, approver, Instant.now());
    }
}