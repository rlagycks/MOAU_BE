package com.moau.moau.accounting.receipt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReceiptDetailDto(
        // --- 1. 영수증 원본(Receipt) 정보 ---
        Long receiptId,
        String imageUrl,
        String description,
        String ocrStatus,
        OcrResultDto ocrResult, // (Nested DTO)

        // --- 2. 승인 요청(ReceiptReview) 정보 (null일 수 있음) ---
        Long reviewId,
        String reviewStatus,
        Long finalAmountCents,
        LocalDate finalTransactionDate,
        String finalDescription,
        String paymentMethod,
        String requesterName,
        String approverName,
        Instant requestedAt,
        Instant approvedAt,
        Instant rejectedAt,
        String rejectReason
) {
    public record OcrResultDto(
            String merchantName,
            Long extractedAmountCents,
            LocalDate extractedTransactionDate,
            String extractedPaymentMethod
    ) {
    }
}