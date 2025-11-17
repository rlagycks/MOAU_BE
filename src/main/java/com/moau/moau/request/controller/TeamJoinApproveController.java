// src/main/java/com/moau/moau/team/controller/TeamJoinApproveController.java
package com.moau.moau.request.controller;

import com.moau.moau.accounting.banking.controller.BankingControllerSwagger;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.jwt.ports.JwtParserPort;
import com.moau.moau.request.dto.request.TeamJoinByCodeRequest;
import com.moau.moau.request.service.TeamJoinApproveService;
import com.moau.moau.request.service.TeamJoinRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams/join-requests")
public class TeamJoinApproveController implements TeamJoinApproveControllerSwagger {

    private final TeamJoinApproveService approveService;
    private final TeamJoinRequestService requestService;
    private final JwtParserPort jwtParser;

    /**
     * 1) 초대코드로 가입 신청 보내기 (멤버가 호출)
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
     * 2) 가입 신청 승인 (대표가 호출)
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
