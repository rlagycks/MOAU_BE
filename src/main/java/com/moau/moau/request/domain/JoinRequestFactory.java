// src/main/java/com/moau/moau/request/domain/JoinRequestFactory.java
package com.moau.moau.request.domain;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.team.domain.Team;
import com.moau.moau.user.domain.User;

import java.lang.reflect.Field;
import java.time.Instant;

public class JoinRequestFactory {

    public static JoinRequest createPending(Team team, User requestUser) {
        JoinRequest req = new JoinRequest();

        setField(req, "team", team);
        setField(req, "requestUser", requestUser);
        setField(req, "status", JoinRequestStatus.PENDING);
        setField(req, "decidedBy", null);
        setField(req, "decidedAt", null);

        return req;
    }

    public static void approve(JoinRequest req, User decider, Instant now) {
        setField(req, "status", JoinRequestStatus.APPROVED);
        setField(req, "decidedBy", decider);
        setField(req, "decidedAt", now);
    }

    public static void reject(JoinRequest req, User decider, Instant now) {
        setField(req, "status", JoinRequestStatus.REJECTED);
        setField(req, "decidedBy", decider);
        setField(req, "decidedAt", now);
    }

    public static void cancel(JoinRequest req, User requester, Instant now) {
        setField(req, "status", JoinRequestStatus.CANCELED);
        setField(req, "decidedBy", requester);
        setField(req, "decidedAt", now);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            // ⭐ 네 프로젝트 규칙: new IllegalStateException + CommonError 메시지
            throw new IllegalStateException(CommonError.JOIN_REQUEST_FIELD_ERROR.getMessage());
        }
    }
}
