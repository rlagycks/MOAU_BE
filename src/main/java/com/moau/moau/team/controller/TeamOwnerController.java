package com.moau.moau.team.controller;

import com.moau.moau.global.security.CheckTeamRole;
import com.moau.moau.team.domain.TeamMemberRole;
import com.moau.moau.team.dto.request.TeamMemberRoleUpdateRequest;
import com.moau.moau.team.dto.request.TeamOwnerTransferRequest;
import com.moau.moau.team.service.TeamMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/{teamId}/owner")
public class TeamOwnerController implements TeamOwnerControllerSwagger{

    private final TeamMemberService teamMemberService;

    @PatchMapping("/members/{userId}/role")
    @CheckTeamRole(TeamMemberRole.OWNER)
    public ResponseEntity<Void> changeMemberRole(
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @RequestBody @Valid TeamMemberRoleUpdateRequest request
    ) {
        TeamMemberRole newRole = request.role();
        teamMemberService.changeMemberRole(teamId, userId, newRole);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/transfer")
    @CheckTeamRole(TeamMemberRole.OWNER)
    public ResponseEntity<Void> transferOwner(
            @PathVariable Long teamId,
            @RequestBody @Valid TeamOwnerTransferRequest request
    ) {
        teamMemberService.transferOwner(teamId, request.newOwnerUserId());
        return ResponseEntity.noContent().build();
    }
}