// src/main/java/com/moau/moau/team/controller/TeamJoinApproveController.java
package com.moau.moau.team.controller;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.team.service.TeamJoinApproveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/join-requests")
public class TeamJoinApproveController {

    private final TeamJoinApproveService approveService;
    private final JwtParserPort jwtParser;

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long teamId,
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String authorization
    ) {
        Long approverUserId = extractUserId(authorization);
        approveService.approve(teamId, requestId, approverUserId);
        return ResponseEntity.ok("가입 신청이 승인되었습니다.");
    }

    private Long extractUserId(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new IllegalArgumentException(CommonError.AUTH_HEADER_MISSING.getMessage());
        }
        if (!authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException(CommonError.AUTH_HEADER_INVALID.getMessage());
        }

        String jwt = authorization.substring(7);
        JwtParserPort.Parsed parsed = jwtParser.parse(jwt);
        if (parsed == null || parsed.subject() == null || parsed.subject().isBlank()) {
            throw new IllegalStateException(CommonError.AUTH_SUBJECT_MISSING.getMessage());
        }

        try {
            return Long.parseLong(parsed.subject());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(CommonError.AUTH_SUBJECT_INVALID.getMessage());
        }
    }
}
