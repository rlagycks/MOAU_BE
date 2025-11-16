// src/main/java/com/moau/moau/team/domain/JoinRequestFactory.java
package com.moau.moau.team.domain;

import com.moau.moau.user.domain.User;

import java.lang.reflect.Field;
import java.time.Instant;

public class JoinRequestFactory {

    public static JoinRequest createPending(Team team, User requestUser) {
        JoinRequest req = new JoinRequest();

        setField(req, "team", team);
        setField(req, "requestUser", requestUser);
        setField(req, "status", "PENDING");  // 신청 시 기본값
        setField(req, "decidedBy", null);
        setField(req, "decidedAt", null);

        return req;
    }

    public static void approve(JoinRequest req, User decider, Instant now) {
        setField(req, "status", "APPROVED");
        setField(req, "decidedBy", decider);
        setField(req, "decidedAt", now);
    }

    public static void reject(JoinRequest req, User decider, Instant now) {
        setField(req, "status", "REJECTED");
        setField(req, "decidedBy", decider);
        setField(req, "decidedAt", now);
    }

    public static void cancel(JoinRequest req, User requester, Instant now) {
        setField(req, "status", "CANCELED");
        setField(req, "decidedBy", requester); // 취소한 사람을 남길지 말지는 정책에 따라
        setField(req, "decidedAt", now);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(
                    "JoinRequest 필드를 설정하는 중 오류가 발생했습니다: " + fieldName, e
            );
        }
    }
}
