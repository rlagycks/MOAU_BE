package com.moau.moau.accounting.receipt.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectReceiptRequestDto(
        @NotBlank(message = "반려 사유는 필수입니다.")
        String reason
) {
}