// src/main/java/com/moau/moau/request/service/TeamJoinRequestService.java
package com.moau.moau.request.service;

import com.moau.moau.request.domain.JoinRequest;
import com.moau.moau.request.domain.JoinRequestFactory;
import com.moau.moau.request.domain.JoinRequestStatus;
import com.moau.moau.request.dto.response.TeamJoinPendingResponse;
import com.moau.moau.request.repository.JoinRequestRepository;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.team.service.TeamAuthorizationService;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamJoinRequestService {

    private final TeamRepository teams;
    private final UserRepository users;
    private final JoinRequestRepository joinRequests;
    private final TeamMemberRepository teamMembers;
    private final TeamAuthorizationService teamAuth;

    /**
     * [멤버 신청] 초대코드로 가입 신청
     */
    @Transactional
    public void requestJoinByInviteCode(String inviteCode, Long userId) {
        Team team = teams.findByInviteCodeAndDeletedAtIsNull(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        User user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 멤버인 경우
        if (teamMembers.existsByTeamAndUser(team, user)) {
            throw new IllegalStateException("이미 이 팀의 멤버입니다.");
        }

        // 이미 PENDING 상태의 가입 신청이 있는 경우
        if (joinRequests.existsByTeamAndRequestUserAndStatus(team, user, JoinRequestStatus.PENDING)) {
            throw new IllegalStateException("이미 가입 신청이 접수된 상태입니다.");
        }

        // PENDING 상태 JoinRequest 생성 후 저장
        JoinRequest req = JoinRequestFactory.createPending(team, user);
        joinRequests.save(req);
    }

    /**
     * [ADMIN + OWNER] 특정 팀의 가입 요청 목록 조회 (PENDING만)
     */
    @Transactional(readOnly = true)
    public List<TeamJoinPendingResponse> getPendingRequests(Long actorUserId, Long teamId) {
        // ADMIN 또는 OWNER 권한 체크
        teamAuth.requireAdminOrOwner(actorUserId, teamId);

        return joinRequests.findAllByTeamIdAndStatus(teamId, JoinRequestStatus.PENDING)
                .stream()
                .map(TeamJoinPendingResponse::from)
                .toList();
    }
}
