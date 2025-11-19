package com.moau.moau.request.service;

import com.moau.moau.request.domain.JoinRequest;
import com.moau.moau.request.domain.JoinRequestFactory;
import com.moau.moau.request.domain.JoinRequestStatus;
import com.moau.moau.request.dto.response.TeamJoinPendingResponse;
import com.moau.moau.request.repository.JoinRequestRepository;
import com.moau.moau.team.domain.Team;
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
public class TeamJoinRequestService {

    private final TeamRepository teams;
    private final UserRepository users;
    private final JoinRequestRepository joinRequests;
    private final TeamMemberRepository teamMembers;
    // private final TeamAuthorizationService teamAuth; // [삭제]

    @Transactional
    public void requestJoinByInviteCode(String inviteCode, Long userId) {
        Team team = teams.findByInviteCodeAndDeletedAtIsNull(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        User user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (teamMembers.existsByTeamAndUser(team, user)) {
            throw new IllegalStateException("이미 이 팀의 멤버입니다.");
        }

        if (joinRequests.existsByTeamAndRequestUserAndStatus(team, user, JoinRequestStatus.PENDING)) {
            throw new IllegalStateException("이미 가입 신청이 접수된 상태입니다.");
        }

        JoinRequest req = JoinRequestFactory.createPending(team, user);
        joinRequests.save(req);
    }

    @Transactional(readOnly = true)
    public List<TeamJoinPendingResponse> getPendingRequests(Long teamId) { // [수정] actorUserId 제거
        // [삭제] teamAuth.requireAdminOrOwner(actorUserId, teamId); (인터셉터가 처리함)

        return joinRequests.findAllByTeamIdAndStatus(teamId, JoinRequestStatus.PENDING)
                .stream()
                .map(TeamJoinPendingResponse::from)
                .toList();
    }
}