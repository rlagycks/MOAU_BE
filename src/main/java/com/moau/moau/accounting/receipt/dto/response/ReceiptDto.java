package com.moau.moau.accounting.receipt.dto.response;

import java.time.Instant;

public record ReceiptDto(
        Long receiptId,
        String uploaderName,
        String imageUrl,
        String description,
        String ocrStatus,
        String reviewStatus,
        Instant createdAt
) {
}