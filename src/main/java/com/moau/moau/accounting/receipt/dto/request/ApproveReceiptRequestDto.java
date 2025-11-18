package com.moau.moau.accounting.receipt.dto.request;

import jakarta.validation.constraints.NotNull;

public record ApproveReceiptRequestDto(
        @NotNull(message = "연결할 은행 계좌 ID는 필수입니다.")
        Long bankAccountId,

        @NotNull(message = "카테고리 ID는 필수입니다.")
        Long categoryId
) {
}