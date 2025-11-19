package com.moau.moau.team.controller;

import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.team.dto.response.TeamMemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "👥 Team - Member", description = "팀 멤버 관리 (조회, 탈퇴, 강퇴) API")
public interface TeamMemberControllerSwagger {

    @Operation(summary = "팀 멤버 목록 조회", description = "(Auth: MEMBER) 해당 팀의 멤버 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    ResponseEntity<List<TeamMemberResponse>> members(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId
    );

    @Operation(summary = "팀 나가기 (탈퇴)", description = "(Auth: MEMBER) 스스로 팀을 나갑니다. (팀장은 불가능)")
    @ApiResponse(responseCode = "204", description = "탈퇴 성공")
    @PostMapping("/leave")
    ResponseEntity<Void> leaveTeam(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId
    );

    @Operation(summary = "멤버 강퇴", description = "(Auth: ADMIN) 특정 멤버를 강제로 내보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "강퇴 성공"),
            @ApiResponse(responseCode = "403", description = "권한 부족 (ADMIN 이상)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{userId}")
    ResponseEntity<Void> kickMember(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "강퇴할 멤버의 User ID", required = true) @PathVariable Long userId
    );
}