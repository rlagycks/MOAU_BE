package com.moau.moau.team.domain;

import com.moau.moau.user.domain.User;

public class TeamFactory {

    public static Team create(User owner, String name, String description, String inviteCode) {
        Team team = new Team();
        team.setOwner(owner);
        team.setName(name);
        team.setDescription(description);
        team.setInviteCode(inviteCode);
        return team;
    }
}
