// src/main/java/com/moau/moau/team/domain/TeamMemberFactory.java
package com.moau.moau.team.domain;

import com.moau.moau.user.domain.User;

import java.time.Instant;

public class TeamMemberFactory {

    public static TeamMember create(
            Team team,
            User user,
            TeamMemberRole role,
            TeamMemberStatus status,
            Long updatedBy
    ) {
        Instant now = Instant.now();
        TeamMemberId id = new TeamMemberId(team.getId(), user.getId());

        return TeamMember.builder()
                .id(id)
                .team(team)
                .user(user)
                .role(role)
                .status(status)
                .joinedAt(now)
                .updatedAt(now)
                .updatedBy(updatedBy)
                .build();
    }
}
