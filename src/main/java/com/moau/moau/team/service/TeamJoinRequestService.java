// src/main/java/com/moau/moau/team/application/TeamJoinRequestService.java
package com.moau.moau.team.service;

import com.moau.moau.team.domain.JoinRequest;
import com.moau.moau.team.domain.JoinRequestFactory;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.repository.JoinRequestRepository;
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

    @Transactional
    public void requestJoin(Long teamId, Long userId) {
        // 그룹 조회
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다."));

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
