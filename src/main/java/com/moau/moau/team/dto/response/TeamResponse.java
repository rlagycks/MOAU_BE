package com.moau.moau.team.dto.response;

import com.moau.moau.team.domain.Team;

public record TeamResponse(
        Long id,
        String name,
        String description,
        String inviteCode
) {
    public static TeamResponse from(Team t) {
        return new TeamResponse(
                t.getId(),
                t.getName(),
                t.getDescription(),
                t.getInviteCode()
        );
    }
}
