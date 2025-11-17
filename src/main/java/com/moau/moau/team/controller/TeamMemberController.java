// src/main/java/com/moau/moau/team/controller/TeamMemberController.java
package com.moau.moau.team.controller;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.team.dto.response.TeamMemberResponse;
import com.moau.moau.team.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/members")
public class TeamMemberController implements TeamMemberControllerSwagger{

    private final TeamMemberService teamMemberService;
    private final JwtParserPort jwtParser;

    /**
     * 팀 멤버 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<TeamMemberResponse>> members(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long teamId
    ) {

        List<TeamMemberResponse> members = teamMemberService.getTeamsMembers(teamId);
        return ResponseEntity.ok(members);
    }

    /**
     * 팀 나가기 (현재 로그인한 유저 기준)
     */
    @PostMapping("/leave")
    public ResponseEntity<Void> leaveTeam(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long teamId
    ) {
        Long userId = extractUserId(authorization);
        teamMemberService.leaveTeam(userId, teamId);
        return ResponseEntity.noContent().build();
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
