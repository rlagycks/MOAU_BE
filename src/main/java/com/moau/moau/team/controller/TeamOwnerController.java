// src/main/java/com/moau/moau/team/controller/TeamOwnerController.java
package com.moau.moau.team.controller;

import com.moau.moau.jwt.ports.JwtParserPort;
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
public class TeamOwnerController {

    private final TeamMemberService teamMemberService;
    private final JwtParserPort jwtParser;

    /**
     * [OWNER 전용] 멤버 역할 변경 (승급/강등)
     * - userId: 역할을 변경할 대상 유저의 id
     */
    @PatchMapping("/members/{userId}/role")
    public ResponseEntity<Void> changeMemberRole(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @RequestBody @Valid TeamMemberRoleUpdateRequest request
    ) {
        Long currentUserId = extractUserId(authorization);
        TeamMemberRole newRole = request.role();
        teamMemberService.changeMemberRole(currentUserId, teamId, userId, newRole);
        return ResponseEntity.noContent().build();
    }

    /**
     * [OWNER 전용] 오너 양도
     * - body: { "newOwnerUserId": Long }
     */
    @PatchMapping("/transfer")
    public ResponseEntity<Void> transferOwner(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long teamId,
            @RequestBody @Valid TeamOwnerTransferRequest request
    ) {
        Long actorUserId = extractUserId(authorization);
        teamMemberService.transferOwner(actorUserId, teamId, request.newOwnerUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Authorization 헤더에서 JWT의 subject(userId)를 추출
     */
    private Long extractUserId(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new IllegalArgumentException("Authorization 헤더가 필요합니다.");
        }

        if (!authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 형식이 잘못되었습니다. Bearer 토큰이어야 합니다.");
        }

        String jwt = authorization.substring(7);

        JwtParserPort.Parsed parsed = jwtParser.parse(jwt);
        if (parsed == null || parsed.subject() == null || parsed.subject().isBlank()) {
            throw new IllegalStateException("JWT에서 사용자 정보를 찾을 수 없습니다.");
        }

        try {
            return Long.parseLong(parsed.subject());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("JWT subject가 숫자 형식이 아닙니다.");
        }
    }
}
