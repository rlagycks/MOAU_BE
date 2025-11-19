package com.moau.moau.accounting.receipt.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReceiptCreateRequestDto(
        @NotBlank(message = "S3 객체 키는 필수입니다.")
        String s3Key,

        @NotBlank(message = "설명은 필수입니다.")
        String description
) {
}