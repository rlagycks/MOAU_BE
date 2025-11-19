// src/main/java/com/moau/moau/team/controller/TeamJoinApproveController.java
package com.moau.moau.request.controller;

import com.moau.moau.accounting.banking.controller.BankingControllerSwagger;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtParserPort;
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
    private final JwtParserPort jwtParser;

    /**
     * 가입 신청 (초대코드)
     */
    @PostMapping
    public ResponseEntity<?> requestJoin(
            @RequestBody @Valid TeamJoinByCodeRequest req,
            @RequestHeader("Authorization") String authorization
    ) {
        Long userId = extractUserId(authorization);
        requestService.requestJoinByInviteCode(req.inviteCode(), userId);
        return ResponseEntity.ok("가입 신청이 접수되었습니다.");
    }

    /**
     * PENDING 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<TeamJoinPendingResponse>> getPending(
            @RequestParam("teamId") Long teamId,
            @RequestHeader("Authorization") String authorization
    ) {
        Long actorUserId = extractUserId(authorization);
        List<TeamJoinPendingResponse> list = requestService.getPendingRequests(actorUserId, teamId);
        return ResponseEntity.ok(list);
    }

    /**
     * 승인
     */
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String authorization
    ) {
        Long approverUserId = extractUserId(authorization);
        approveService.approve(requestId, approverUserId);
        return ResponseEntity.ok("가입 신청이 승인되었습니다.");
    }

    /**
     * 거절
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String authorization
    ) {
        Long approverUserId = extractUserId(authorization);
        approveService.reject(requestId, approverUserId);
        return ResponseEntity.ok("가입 신청이 거절되었습니다.");
    }

    /**
     * JWT → userId 추출 (네 코드 그대로)
     */
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
