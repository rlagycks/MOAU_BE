package com.moau.moau.team.controller;

import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.global.security.CheckTeamRole;
import com.moau.moau.team.domain.TeamMemberRole;
import com.moau.moau.team.dto.response.TeamMemberResponse;
import com.moau.moau.team.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/members")
public class TeamMemberController implements TeamMemberControllerSwagger {

    private final TeamMemberService teamMemberService;

    @GetMapping
    @CheckTeamRole(TeamMemberRole.MEMBER) // [적용]
    public ResponseEntity<List<TeamMemberResponse>> members(
            @PathVariable Long teamId
    ) {
        List<TeamMemberResponse> members = teamMemberService.getTeamsMembers(teamId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/leave")
    @CheckTeamRole(TeamMemberRole.MEMBER) // [적용]
    public ResponseEntity<Void> leaveTeam(
            @PathVariable Long teamId
    ) {
        teamMemberService.leaveTeam(teamId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    @CheckTeamRole(TeamMemberRole.ADMIN) // [적용] 관리자 이상 강퇴 가능
    public ResponseEntity<Void> kickMember(
            @PathVariable Long teamId,
            @PathVariable Long userId
    ) {
        teamMemberService.kickMember(teamId, userId);
        return ResponseEntity.noContent().build();
    }
}