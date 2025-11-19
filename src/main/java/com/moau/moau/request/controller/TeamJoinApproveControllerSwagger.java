package com.moau.moau.request.controller;

import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.request.dto.request.TeamJoinByCodeRequest;
import com.moau.moau.request.dto.response.TeamJoinPendingResponse;
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

@Tag(name = "💌 Team - Join Request", description = "팀 가입 신청 및 승인/거절 API")
public interface TeamJoinApproveControllerSwagger {

    @Operation(summary = "가입 신청 (초대코드)", description = "초대 코드를 입력하여 팀 가입을 신청합니다.")
    @ApiResponse(responseCode = "200", description = "신청 성공")
    @PostMapping
    ResponseEntity<?> requestJoin(
            @RequestBody @Valid TeamJoinByCodeRequest req
    );

    @Operation(summary = "가입 대기 목록 조회", description = "(Auth: ADMIN) 승인 대기 중인 가입 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "권한 부족", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{teamId}") // (경로 수정 반영)
    ResponseEntity<List<TeamJoinPendingResponse>> getPending(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId
    );

    @Operation(summary = "가입 승인", description = "(Auth: ADMIN) 가입 요청을 승인합니다.")
    @ApiResponse(responseCode = "200", description = "승인 성공")
    @PostMapping("/{teamId}/{requestId}/approve") // (경로 수정 반영)
    ResponseEntity<?> approve(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "요청 ID", required = true) @PathVariable Long requestId
    );

    @Operation(summary = "가입 거절", description = "(Auth: ADMIN) 가입 요청을 거절합니다.")
    @ApiResponse(responseCode = "200", description = "거절 성공")
    @PostMapping("/{teamId}/{requestId}/reject") // (경로 수정 반영)
    ResponseEntity<?> reject(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "요청 ID", required = true) @PathVariable Long requestId
    );
}