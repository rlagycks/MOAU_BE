package com.moau.moau.accounting.receipt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReceiptReviewDto(
        Long reviewId,
        Long receiptId,
        String status,
        Long amountCents,
        LocalDate transactionDate,
        String merchantName,
        String paymentMethod,
        String description,

        Instant requestedAt,
        Instant approvedAt,
        Instant rejectedAt,

        String requesterName,
        String approverName,
        String rejectReason,

        String receiptImageUrl
) {
}