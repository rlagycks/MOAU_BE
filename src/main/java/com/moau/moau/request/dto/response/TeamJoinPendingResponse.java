// src/main/java/com/moau/moau/request/dto/response/TeamJoinPendingResponse.java
package com.moau.moau.request.dto.response;

import com.moau.moau.request.domain.JoinRequest;
import com.moau.moau.request.domain.JoinRequestStatus;

import java.time.Instant;

public record TeamJoinPendingResponse(
        Long requestId,
        Long teamId,
        Long requestUserId,
        String requestUserNickname,
        JoinRequestStatus status,
        Instant requestedAt,
        Instant decidedAt
) {
    public static TeamJoinPendingResponse from(JoinRequest jr) {
        return new TeamJoinPendingResponse(
                jr.getId(),
                jr.getTeam().getId(),
                jr.getRequestUser().getId(),
                jr.getRequestUser().getNickname(),
                jr.getStatus(),
                jr.getCreatedAt(),  // BaseId에 createdAt 있다고 가정
                jr.getDecidedAt()
        );
    }
}
