package com.moau.moau.team.controller;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.team.dto.request.TeamCreateRequest;
import com.moau.moau.team.dto.request.TeamUpdateRequest;
import com.moau.moau.team.dto.response.TeamDetailResponse;
import com.moau.moau.team.dto.response.TeamResponse;

import com.moau.moau.team.service.TeamCommandService;
import com.moau.moau.team.service.TeamQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
public class TeamController implements TeamContollerSwagger{

    private final TeamCommandService teamCommands;
    private final TeamQueryService teamQueries;
    private final JwtParserPort jwtParser;

    @PostMapping
    public ResponseEntity<TeamDetailResponse> createTeam(
            @RequestHeader("Authorization") String auth,
            @RequestBody @Valid TeamCreateRequest req
    ) {
        Long userId = extractUserId(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamCommands.createTeam(userId, req));
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeams(
            @RequestHeader("Authorization") String auth
    ) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(teamQueries.getMyTeams(userId));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponse> getTeamDetail(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long teamId
    ) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(teamQueries.getTeamDetail(userId, teamId));
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponse> updateTeam(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long teamId,
            @RequestBody @Valid TeamUpdateRequest req
    ) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(teamCommands.updateTeam(userId, teamId, req));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long teamId
    ) {
        Long userId = extractUserId(auth);
        teamCommands.deleteTeam(userId, teamId);
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
