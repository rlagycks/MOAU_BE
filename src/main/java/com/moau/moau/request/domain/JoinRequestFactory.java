// src/main/java/com/moau/moau/request/domain/JoinRequestFactory.java
package com.moau.moau.request.domain;

import com.moau.moau.team.domain.Team;
import com.moau.moau.user.domain.User;

public class JoinRequestFactory {

    public static JoinRequest createPending(Team team, User requestUser) {
        return JoinRequest.builder()
                .team(team)
                .requestUser(requestUser)
                .status(JoinRequestStatus.PENDING)
                .decidedBy(null)
                .decidedAt(null)
                .build();
    }
}
