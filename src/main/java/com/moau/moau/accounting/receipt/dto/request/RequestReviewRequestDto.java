package com.moau.moau.accounting.receipt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record RequestReviewRequestDto(
        @NotNull(message = "금액은 필수입니다.")
        @Positive(message = "금액은 0보다 커야 합니다.")
        Long amountCents,

        @NotNull(message = "거래 일자는 필수입니다.")
        LocalDate transactionDate,

        @NotBlank(message = "설명은 필수입니다.")
        String description,

        @NotBlank(message = "가맹점명은 필수입니다.")
        String merchantName,

        String paymentMethod // (선택 사항)
) {
}