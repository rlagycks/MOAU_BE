// src/main/java/com/moau/moau/team/service/TeamJoinRequestService.java
package com.moau.moau.request.service;

import com.moau.moau.request.domain.JoinRequest;
import com.moau.moau.request.domain.JoinRequestFactory;
import com.moau.moau.team.domain.Team;
import com.moau.moau.request.repository.JoinRequestRepository;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamJoinRequestService {

    private final TeamRepository teams;
    private final UserRepository users;
    private final JoinRequestRepository joinRequests;
    private final TeamMemberRepository teamMembers;

    /**
     * 초대코드로 가입 신청
     */
    @Transactional
    public void requestJoinByInviteCode(String inviteCode, Long userId) {
        // 초대코드로 그룹 조회
        Team team = teams.findByInviteCodeAndDeletedAtIsNull(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        // 신청자 조회
        User user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 멤버인 경우
        if (teamMembers.existsByTeamAndUser(team, user)) {
            throw new IllegalStateException("이미 이 그룹의 멤버입니다.");
        }

        // 이미 PENDING 상태의 가입 신청이 있는 경우
        if (joinRequests.existsByTeamAndRequestUserAndStatus(team, user, "PENDING")) {
            throw new IllegalStateException("이미 가입 신청이 접수된 상태입니다.");
        }

        // PENDING 상태 JoinRequest 생성
        JoinRequest req = JoinRequestFactory.createPending(team, user);

        // 저장
        joinRequests.save(req);
    }
}
