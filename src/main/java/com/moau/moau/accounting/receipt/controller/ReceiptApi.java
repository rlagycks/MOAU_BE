package com.moau.moau.accounting.receipt.controller;

import com.moau.moau.accounting.receipt.dto.request.PresignedUrlRequestDto;
import com.moau.moau.accounting.receipt.dto.request.ReceiptCreateRequestDto;
import com.moau.moau.accounting.receipt.dto.request.RequestReviewRequestDto;
import com.moau.moau.accounting.receipt.dto.response.PresignedUrlResponseDto;
import com.moau.moau.accounting.receipt.dto.response.ReceiptDetailDto;
import com.moau.moau.accounting.receipt.dto.response.ReceiptDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "🧾 Accounting - Receipt", description = "영수증(업로드, 조회) API")
@RequestMapping("/api/teams/{teamId}/accounting/receipts")
public interface ReceiptApi {

    @Operation(summary = "S3 업로드 URL 발급", description = "(Auth: MEMBER)")
    @ApiResponse(responseCode = "200", description = "발급 성공",
            content = @Content(schema = @Schema(implementation = PresignedUrlResponseDto.class)))
    @PostMapping("/upload-url")
    ResponseEntity<ResponseDto<PresignedUrlResponseDto>> createPresignedUrl(
            @Parameter(description = "업로드할 파일명") @Valid @RequestBody PresignedUrlRequestDto requestDto
    );

    @Operation(summary = "영수증 정보 등록", description = "(Auth: MEMBER) S3 업로드 완료 후 s3Key를 전송합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공 (OCR 비동기 처리 시작)",
            content = @Content(schema = @Schema(implementation = ReceiptDto.class)))
    @PostMapping
    ResponseEntity<ResponseDto<ReceiptDto>> createReceipt(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "S3 Key 및 설명") @Valid @RequestBody ReceiptCreateRequestDto requestDto
    );

    @Operation(summary = "영수증 상세 조회", description = "(Auth: MEMBER) OCR 결과 및 승인 상태를 함께 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ReceiptDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "영수증을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{receiptId}")
    ResponseEntity<ResponseDto<ReceiptDetailDto>> getReceiptDetail(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "영수증 ID", required = true) @PathVariable Long receiptId
    );

    @Operation(summary = "관리자 승인 요청 생성", description = "(Auth: MEMBER) OCR 결과를 확정하여 관리자에게 승인을 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(schema = @Schema(implementation = ReceiptReviewDto.class))),
            @ApiResponse(responseCode = "404", description = "영수증을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 승인 요청되었거나 처리된 영수증",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{receiptId}/request-review")
    ResponseEntity<ResponseDto<ReceiptReviewDto>> requestReview(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "영수증 ID", required = true) @PathVariable Long receiptId,
            @Parameter(description = "사용자가 확정한 최종 영수증 정보") @Valid @RequestBody RequestReviewRequestDto requestDto
    );
}