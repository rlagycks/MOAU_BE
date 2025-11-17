// src/main/java/com/moau/moau/team/controller/TeamContollerSwagger.java
package com.moau.moau.team.controller;

import com.moau.moau.team.dto.request.TeamCreateRequest;
import com.moau.moau.team.dto.request.TeamUpdateRequest;
import com.moau.moau.team.dto.response.TeamDetailResponse;
import com.moau.moau.team.dto.response.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Team", description = "팀 생성 / 조회 / 수정 / 삭제 API")
public interface TeamContollerSwagger {

    @Operation(
            summary = "팀 생성",
            description = "현재 로그인한 사용자를 오너로 하는 팀을 생성합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "팀 생성 성공",
            content = @Content(schema = @Schema(implementation = TeamDetailResponse.class))
    )
    ResponseEntity<TeamDetailResponse> createTeam(
            @Parameter(description = "Bearer 토큰 (예: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...)")
            String auth,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 팀 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TeamCreateRequest.class))
            )
            TeamCreateRequest req
    );

    @Operation(
            summary = "내가 속한 팀 목록 조회",
            description = "현재 로그인한 사용자가 오너 / 관리자 / 일반 멤버로 속한 모든 팀 목록을 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = TeamResponse.class))
    )
    ResponseEntity<List<TeamResponse>> getTeams(
            @Parameter(description = "Bearer 토큰")
            String auth
    );

    @Operation(
            summary = "팀 상세 조회",
            description = "팀 ID로 팀 상세 정보를 조회합니다. 권한이 없으면 예외가 발생합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = TeamDetailResponse.class))
    )
    ResponseEntity<TeamDetailResponse> getTeamDetail(
            @Parameter(description = "Bearer 토큰")
            String auth,
            @Parameter(description = "조회할 팀 ID", example = "1")
            Long teamId
    );

    @Operation(
            summary = "팀 수정",
            description = "팀 오너만 팀 이름/설명을 수정할 수 있습니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(schema = @Schema(implementation = TeamDetailResponse.class))
    )
    ResponseEntity<TeamDetailResponse> updateTeam(
            @Parameter(description = "Bearer 토큰")
            String auth,
            @Parameter(description = "수정할 팀 ID", example = "1")
            Long teamId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 팀 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TeamUpdateRequest.class))
            )
            TeamUpdateRequest req
    );

    @Operation(
            summary = "팀 삭제",
            description = "팀 오너만 팀을 삭제할 수 있습니다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "삭제 성공"
    )
    ResponseEntity<Void> deleteTeam(
            @Parameter(description = "Bearer 토큰")
            String auth,
            @Parameter(description = "삭제할 팀 ID", example = "1")
            Long teamId
    );
}
