// src/main/java/com/moau/moau/team/service/TeamAuthorizationService.java
package com.moau.moau.team.service;

import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberRole;
import com.moau.moau.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamAuthorizationService {

    private final TeamMemberRepository teamMembers;

    /**
     * 팀 멤버인지 확인 (MEMBER / ADMIN / OWNER 모두 통과)
     */
    public TeamMember requireMember(Long userId, Long teamId) {
        return teamMembers.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() ->
                        new IllegalStateException("해당 팀의 멤버가 아닙니다.")
                );
    }

    /**
     * ADMIN 또는 OWNER만 허용
     */
    public TeamMember requireAdminOrOwner(Long userId, Long teamId) {
        TeamMember member = requireMember(userId, teamId);
        TeamMemberRole role = member.getRole();

        if (role == TeamMemberRole.ADMIN || role == TeamMemberRole.OWNER) {
            return member;
        }

        throw new IllegalStateException("이 기능을 사용할 수 있는 권한이 없습니다.");
    }

    /**
     * OWNER만 허용
     */
    public TeamMember requireOwner(Long userId, Long teamId) {
        TeamMember member = requireMember(userId, teamId);

        if (member.getRole() != TeamMemberRole.OWNER) {
            throw new IllegalStateException("오너만 사용할 수 있는 기능입니다.");
        }

        return member;
    }
}
