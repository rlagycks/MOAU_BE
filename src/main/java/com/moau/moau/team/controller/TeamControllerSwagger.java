package com.moau.moau.team.controller;

import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.team.dto.request.TeamCreateRequest;
import com.moau.moau.team.dto.request.TeamUpdateRequest;
import com.moau.moau.team.dto.response.TeamDetailResponse;
import com.moau.moau.team.dto.response.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "👥 Team", description = "팀 생성, 조회, 수정, 삭제 API")
public interface TeamControllerSwagger {

    @Operation(summary = "팀 생성", description = "새로운 팀을 생성합니다. 생성자는 자동으로 OWNER가 됩니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(schema = @Schema(implementation = TeamDetailResponse.class)))
    @PostMapping
    ResponseEntity<TeamDetailResponse> createTeam(
            @RequestBody @Valid TeamCreateRequest req
    );

    @Operation(summary = "내 팀 목록 조회", description = "내가 속한 팀 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    ResponseEntity<List<TeamResponse>> getTeams();

    @Operation(summary = "팀 상세 조회", description = "(Auth: MEMBER) 팀의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "팀 멤버가 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "팀을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{teamId}")
    ResponseEntity<TeamDetailResponse> getTeamDetail(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId
    );

    @Operation(summary = "팀 정보 수정", description = "(Auth: OWNER) 팀 이름이나 설명을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (OWNER만 가능)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{teamId}")
    ResponseEntity<TeamDetailResponse> updateTeam(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @RequestBody @Valid TeamUpdateRequest req
    );

    @Operation(summary = "팀 삭제", description = "(Auth: OWNER) 팀을 삭제(Soft Delete)합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{teamId}")
    ResponseEntity<Void> deleteTeam(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId
    );
}