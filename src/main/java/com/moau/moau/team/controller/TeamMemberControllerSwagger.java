// src/main/java/com/moau/moau/team/controller/TeamMemberControllerSwagger.java
package com.moau.moau.team.controller;

import com.moau.moau.team.dto.response.TeamMemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Team Member", description = "팀 멤버 조회 / 팀 나가기 API")
public interface TeamMemberControllerSwagger {

    @Operation(
            summary = "팀 멤버 목록 조회",
            description = "특정 팀의 멤버 목록을 조회합니다. (인증 필요: 팀에 속한 사용자만 호출 가능)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = TeamMemberResponse.class))
    )
    ResponseEntity<List<TeamMemberResponse>> members(
            @Parameter(description = "Bearer 토큰")
            String authorization,
            @Parameter(description = "팀 ID", example = "1")
            Long teamId
    );

    @Operation(
            summary = "팀 나가기",
            description = "현재 로그인한 사용자가 해당 팀에서 나갑니다. 팀장은 나갈 수 없습니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "팀 나가기 성공"
    )
    ResponseEntity<Void> leaveTeam(
            @Parameter(description = "Bearer 토큰")
            String authorization,
            @Parameter(description = "팀 ID", example = "1")
            Long teamId
    );
}
