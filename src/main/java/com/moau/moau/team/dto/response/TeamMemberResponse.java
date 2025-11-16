// src/main/java/com/moau/moau/team/dto/response/TeamMemberResponse.java
package com.moau.moau.team.dto.response;

import com.moau.moau.team.domain.TeamMember;

public record TeamMemberResponse(
        Long userId,
        String nickname,
        String role,
        String status
) {
    public static TeamMemberResponse from(TeamMember m) {
        return new TeamMemberResponse(
                m.getUser().getId(),
                m.getUser().getNickname(),
                m.getRole(),
                m.getStatus()
        );
    }
}
