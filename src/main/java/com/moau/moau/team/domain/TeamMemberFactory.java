// src/main/java/com/moau/moau/team/domain/TeamMemberFactory.java
package com.moau.moau.team.domain;

import com.moau.moau.user.domain.User;

import java.lang.reflect.Field;
import java.time.Instant;

public class TeamMemberFactory {

    public static TeamMember create(Team team, User user,
                                    String role, String status, Long updatedBy) {

        // 엔티티와 ID는 같은 패키지라서 protected 생성자 호출 가능
        TeamMember member = new TeamMember();
        TeamMemberId id = new TeamMemberId();
        Instant now = Instant.now();

        // 복합키 값 설정 (teamId, userId)
        setField(id, "teamId", team.getId());   // Team의 PK
        setField(id, "userId", user.getId());   // User의 PK

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
            throw new IllegalStateException(
                    "필드 값을 설정하는 중 오류가 발생했습니다: " + fieldName, e
            );
        }
    }
}
