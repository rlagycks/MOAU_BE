package com.moau.moau.request.controller;

import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.global.security.CheckTeamRole;
import com.moau.moau.team.domain.TeamMemberRole;
import com.moau.moau.request.dto.request.TeamJoinByCodeRequest;
import com.moau.moau.request.dto.response.TeamJoinPendingResponse;
import com.moau.moau.request.service.TeamJoinApproveService;
import com.moau.moau.request.service.TeamJoinRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/join-requests")
public class TeamJoinApproveController implements TeamJoinApproveControllerSwagger {

    private final TeamJoinRequestService requestService;
    private final TeamJoinApproveService approveService;

    @PostMapping
    public ResponseEntity<?> requestJoin(
            @RequestBody @Valid TeamJoinByCodeRequest req
    ) {
        Long userId = SecurityUtil.getCurrentUserId(); // [수정]
        requestService.requestJoinByInviteCode(req.inviteCode(), userId);
        return ResponseEntity.ok("가입 신청이 접수되었습니다.");
    }

    @GetMapping("/{teamId}")
    @CheckTeamRole(TeamMemberRole.ADMIN) // [적용]
    public ResponseEntity<List<TeamJoinPendingResponse>> getPending(
            @PathVariable Long teamId
    ) {
        List<TeamJoinPendingResponse> list = requestService.getPendingRequests(teamId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{teamId}/{requestId}/approve")
    @CheckTeamRole(TeamMemberRole.ADMIN) // [적용]
    public ResponseEntity<?> approve(
            @PathVariable Long teamId,
            @PathVariable Long requestId
    ) {
        approveService.approve(requestId);
        return ResponseEntity.ok("가입 신청이 승인되었습니다.");
    }

    @PostMapping("/{teamId}/{requestId}/reject")
    @CheckTeamRole(TeamMemberRole.ADMIN) // [적용]
    public ResponseEntity<?> reject(
            @PathVariable Long teamId,
            @PathVariable Long requestId
    ) {
        approveService.reject(requestId);
        return ResponseEntity.ok("가입 신청이 거절되었습니다.");
    }
}