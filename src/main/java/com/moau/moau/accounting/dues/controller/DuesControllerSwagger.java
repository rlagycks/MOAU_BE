package com.moau.moau.accounting.dues.controller;

import com.moau.moau.accounting.dues.dto.request.DuesStatusUpdateRequestDto;
import com.moau.moau.accounting.dues.dto.response.DuesCycleDetailDto;
import com.moau.moau.accounting.dues.dto.response.DuesCycleDto;
import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.global.payload.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "💸 Accounting - Dues", description = "회비 관리 (납부 주기, 현황, 체크) API")
@RequestMapping("/api/teams/{teamId}/accounting/dues")
public interface DuesControllerSwagger {

    @Operation(summary = "회비 주기 목록 조회", description = "(Auth: ADMIN) 팀 설정에 따른 회비 납부 주기(월/분기 등) 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/cycles")
    ResponseEntity<ResponseDto<List<DuesCycleDto>>> getCycles(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId
    );

    @Operation(summary = "특정 주기 납부 현황 조회", description = "(Auth: ADMIN) 특정 날짜가 포함된 주기의 납부 현황을 조회합니다. (데이터가 없으면 자동 생성)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "팀 회비 설정이 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/cycles/status")
    ResponseEntity<ResponseDto<DuesCycleDetailDto>> getCycleStatus(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", example = "2025-11-20")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate
    );

    @Operation(summary = "납부 상태 변경 (체크/해제)", description = "(Auth: ADMIN) 특정 멤버의 납부 상태를 변경하고 뱅킹 내역을 기록합니다.")
    @ApiResponse(responseCode = "200", description = "변경 성공 (갱신된 전체 현황 반환)")
    @PatchMapping("/cycles/{cycleId}/members/{userId}/status")
    ResponseEntity<ResponseDto<DuesCycleDetailDto>> updateMemberStatus(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "회비 주기 ID", required = true) @PathVariable Long cycleId,
            @Parameter(description = "대상 멤버 User ID", required = true) @PathVariable Long userId,
            @Valid @RequestBody DuesStatusUpdateRequestDto requestDto
    );
}