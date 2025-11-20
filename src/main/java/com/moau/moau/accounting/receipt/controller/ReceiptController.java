package com.moau.moau.accounting.receipt.controller;

import com.moau.moau.accounting.receipt.dto.request.PresignedUrlRequestDto;
import com.moau.moau.accounting.receipt.dto.request.ReceiptCreateRequestDto;
import com.moau.moau.accounting.receipt.dto.request.RequestReviewRequestDto;
import com.moau.moau.accounting.receipt.dto.response.PresignedUrlResponseDto;
import com.moau.moau.accounting.receipt.dto.response.ReceiptDetailDto;
import com.moau.moau.accounting.receipt.dto.response.ReceiptDto;
import com.moau.moau.accounting.receipt.dto.response.ReceiptReviewDto;
import com.moau.moau.accounting.receipt.service.ReceiptCommandService;
import com.moau.moau.accounting.receipt.service.ReceiptQueryService;
import com.moau.moau.accounting.receipt.service.ReceiptReviewCommandService;
import com.moau.moau.global.payload.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams/{teamId}/accounting/receipts")
@RequiredArgsConstructor
public class ReceiptController  implements ReceiptApi  {

    private final ReceiptCommandService receiptCommandService;
    private final ReceiptQueryService receiptQueryService;
    private final ReceiptReviewCommandService receiptReviewCommandService;

    @Override
    @PostMapping("/upload-url")
    public ResponseEntity<ResponseDto<PresignedUrlResponseDto>> createPresignedUrl(
            @Valid @RequestBody PresignedUrlRequestDto requestDto
    ) {
        PresignedUrlResponseDto responseDto = receiptCommandService.createPresignedUrl(requestDto.filename());
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }

    @Override
    @PostMapping
    public ResponseEntity<ResponseDto<ReceiptDto>> createReceipt(
            @PathVariable Long teamId,
            @Valid @RequestBody ReceiptCreateRequestDto requestDto
    ) {
        ReceiptDto responseDto = receiptCommandService.createReceipt(teamId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.data(responseDto));
    }

    @Override
    @GetMapping("/{receiptId}")
    public ResponseEntity<ResponseDto<ReceiptDetailDto>> getReceiptDetail(
            @PathVariable Long teamId,
            @PathVariable Long receiptId
    ) {
        ReceiptDetailDto responseDto = receiptQueryService.getReceiptDetail(teamId, receiptId);
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }

    @Override
    @PostMapping("/{receiptId}/request-review")
    public ResponseEntity<ResponseDto<ReceiptReviewDto>> requestReview(
            @PathVariable Long teamId,
            @PathVariable Long receiptId,
            @Valid @RequestBody RequestReviewRequestDto requestDto
    ) {
        ReceiptReviewDto responseDto = receiptReviewCommandService.requestReview(teamId, receiptId, requestDto);
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }
}