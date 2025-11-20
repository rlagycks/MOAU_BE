package com.moau.moau.team.controller;

import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.global.security.CheckTeamRole;
import com.moau.moau.team.domain.TeamMemberRole;
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
public class TeamController implements TeamControllerSwagger {

    private final TeamCommandService teamCommands;
    private final TeamQueryService teamQueries;

    @PostMapping
    public ResponseEntity<TeamDetailResponse> createTeam(
            @RequestBody @Valid TeamCreateRequest req
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamCommands.createTeam(userId, req));
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeams() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(teamQueries.getMyTeams(userId));
    }

    @GetMapping("/{teamId}")
    @CheckTeamRole(TeamMemberRole.MEMBER) // [적용] 멤버만 조회 가능
    public ResponseEntity<TeamDetailResponse> getTeamDetail(
            @PathVariable Long teamId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(teamQueries.getTeamDetail(userId, teamId));
    }

    @PutMapping("/{teamId}")
    @CheckTeamRole(TeamMemberRole.OWNER) // [적용] 오너만 수정 가능
    public ResponseEntity<TeamDetailResponse> updateTeam(
            @PathVariable Long teamId,
            @RequestBody @Valid TeamUpdateRequest req
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(teamCommands.updateTeam(userId, teamId, req));
    }

    @DeleteMapping("/{teamId}")
    @CheckTeamRole(TeamMemberRole.OWNER) // [적용] 오너만 삭제 가능
    public ResponseEntity<Void> deleteTeam(
            @PathVariable Long teamId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        teamCommands.deleteTeam(userId, teamId);
        return ResponseEntity.noContent().build();
    }
}