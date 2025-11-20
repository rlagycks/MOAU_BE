package com.moau.moau.accounting.receipt.controller;

import com.moau.moau.accounting.receipt.domain.ReviewStatus;
import com.moau.moau.accounting.receipt.dto.request.ApproveReceiptRequestDto;
import com.moau.moau.accounting.receipt.dto.request.RejectReceiptRequestDto;
import com.moau.moau.accounting.receipt.dto.response.ReceiptReviewDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "🧾 Accounting - Review", description = "영수증(승인/반려) API")
@RequestMapping("/api/teams/{teamId}/accounting/reviews/receipts")
public interface ReceiptReviewApi {

    @Operation(summary = "승인/반려/대기 목록 조회", description = "(Auth: ADMIN)")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = Page.class))) // (Page<ReceiptReviewDto>)
    @GetMapping
    ResponseEntity<ResponseDto<Page<ReceiptReviewDto>>> getPendingReviews(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "조회할 상태 (기본값: PENDING_APPROVAL)")
            @RequestParam(required = false, defaultValue = "PENDING_APPROVAL") ReviewStatus status,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "영수증 최종 승인", description = "(Auth: ADMIN) 승인 시 뱅킹 도메인에 지출이 기록됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공",
                    content = @Content(schema = @Schema(implementation = ReceiptReviewDto.class))),
            @ApiResponse(responseCode = "404", description = "승인 요청을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 처리 완료된 요청임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{reviewId}/approve")
    ResponseEntity<ResponseDto<ReceiptReviewDto>> approveReceipt(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "승인 요청 ID", required = true) @PathVariable Long reviewId,
            @Parameter(description = "은행 계좌 ID 및 카테고리 ID") @Valid @RequestBody ApproveReceiptRequestDto requestDto
    );

    @Operation(summary = "영수증 반려", description = "(Auth: ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "반려 성공",
                    content = @Content(schema = @Schema(implementation = ReceiptReviewDto.class))),
            @ApiResponse(responseCode = "404", description = "승인 요청을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 처리 완료된 요청임",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{reviewId}/reject")
    ResponseEntity<ResponseDto<ReceiptReviewDto>> rejectReceipt(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "승인 요청 ID", required = true) @PathVariable Long reviewId,
            @Parameter(description = "반려 사유") @Valid @RequestBody RejectReceiptRequestDto requestDto
    );
}