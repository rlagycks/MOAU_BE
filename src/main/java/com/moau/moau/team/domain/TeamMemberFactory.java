// src/main/java/com/moau/moau/team/domain/TeamMemberFactory.java
package com.moau.moau.team.domain;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.user.domain.User;

import java.lang.reflect.Field;
import java.time.Instant;

public class TeamMemberFactory {

    public static TeamMember create(
            Team team,
            User user,
            TeamMemberRole role,
            TeamMemberStatus status,
            Long updatedBy
    ) {

        TeamMember member = new TeamMember();
        TeamMemberId id = new TeamMemberId();
        Instant now = Instant.now();

        // 복합키 설정
        setField(id, "teamId", team.getId());
        setField(id, "userId", user.getId());

        // TeamMember 필드 설정
        setField(member, "id", id);
        setField(member, "team", team);
        setField(member, "user", user);
        setField(member, "role", role);
        setField(member, "status", status);
        setField(member, "joinedAt", now);
        setField(member, "updatedAt", now);
        setField(member, "updatedBy", updatedBy);

        return member;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 통일된 방식: CommonError.getMessage()
            throw new IllegalStateException(CommonError.TEAM_MEMBER_FIELD_ERROR.getMessage());
        }
    }
}
