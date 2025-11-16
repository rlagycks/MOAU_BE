package com.moau.moau.team.domain;

import com.moau.moau.user.domain.User;

public class TeamFactory {

    // 같은 패키지라서 Team의 protected 생성자 사용 가능
    public static Team create(User owner, String name, String description, String inviteCode) {
        Team team = new Team();   // 여기서는 에러 안 남
        team.setOwner(owner);
        team.setName(name);
        team.setDescription(description);
        team.setInviteCode(inviteCode);
        return team;
    }
}
