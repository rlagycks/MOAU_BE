package com.moau.moau.request.service;

import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
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

    /** 초대코드로 가입 신청 */
    @Transactional
    public void requestJoinByInviteCode(String inviteCode, Long userId) {

        Team team = teams.findByInviteCodeAndDeletedAtIsNull(inviteCode)
                .orElseThrow(() ->
                        new BusinessException(CommonError.INVITE_CODE_INVALID)
                );

        User user = users.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(CommonError.USER_NOT_FOUND)
                );

        if (teamMembers.existsByTeamAndUser(team, user)) {
            throw new BusinessException(CommonError.TEAM_MEMBER_ALREADY_EXISTS);
        }

        if (joinRequests.existsByTeamAndRequestUserAndStatus(team, user, JoinRequestStatus.PENDING)) {
            throw new BusinessException(CommonError.JOIN_REQUEST_ALREADY_EXISTS);
        }

        JoinRequest req = JoinRequestFactory.createPending(team, user);
        joinRequests.save(req);
    }

    /** 팀의 모든 Pending 요청 조회 */
    @Transactional(readOnly = true)
    public List<TeamJoinPendingResponse> getPendingRequests(Long teamId) {
        return joinRequests.findAllByTeamIdAndStatus(teamId, JoinRequestStatus.PENDING)
                .stream()
                .map(TeamJoinPendingResponse::from)
                .toList();
    }
}
