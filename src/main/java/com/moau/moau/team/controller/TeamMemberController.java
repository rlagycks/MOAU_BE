// src/main/java/com/moau/moau/team/controller/TeamMemberController.java
package com.moau.moau.team.controller;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.team.dto.response.TeamMemberResponse;
import com.moau.moau.team.service.TeamJoinRequestService;
import com.moau.moau.team.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/members")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;
    private final TeamJoinRequestService teamJoinRequestService;
    private final JwtParserPort jwtParser;

    /**
     * 현재 로그인한 유저가 해당 팀에 가입 신청
     */
    @PostMapping
    public ResponseEntity<?> join(
            @PathVariable Long teamId,
            @RequestHeader("Authorization") String authorization
    ) {
        Long userId = extractUserId(authorization);
        teamJoinRequestService.requestJoin(teamId, userId);
        return ResponseEntity.ok("가입 신청이 접수되었습니다.");
    }

    /**
     * 팀 멤버 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<TeamMemberResponse>> members(
            @PathVariable Long teamId
    ) {
        List<TeamMemberResponse> members = teamMemberService.getGroupMembers(teamId);
        return ResponseEntity.ok(members);
    }

    private Long extractUserId(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new IllegalArgumentException(CommonError.AUTH_HEADER_MISSING.getMessage());
        }
        if (!authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException(CommonError.AUTH_HEADER_INVALID.getMessage());
        }

        String jwt = authorization.substring(7);
        var parsed = jwtParser.parse(jwt);
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
