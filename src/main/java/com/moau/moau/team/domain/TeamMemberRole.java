package com.moau.moau.team.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamMemberRole {
    OWNER(3),
    ADMIN(2),
    MEMBER(1);

    private final int power;

    public boolean isAtLeast(TeamMemberRole requiredRole) {
        return this.power >= requiredRole.getPower();
    }
}