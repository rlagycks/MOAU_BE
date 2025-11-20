package com.moau.moau.team.dto.response;

import com.moau.moau.team.domain.DuesPeriod; // Enum import
import com.moau.moau.team.domain.Team;

import java.time.Instant;

public record TeamDetailResponse(
        Long id,
        Long ownerId,
        String name,
        String description,
        String inviteCode,
        Instant createdAt,

        DuesPeriod duesPeriod,
        Long duesAmount
) {
    public static TeamDetailResponse from(Team t) {
        return new TeamDetailResponse(
                t.getId(),
                t.getOwner().getId(),
                t.getName(),
                t.getDescription(),
                t.getInviteCode(),
                t.getCreatedAt(),

                t.getDuesPeriod(),
                t.getDuesAmount()
        );
    }
}