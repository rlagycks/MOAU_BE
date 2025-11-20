package com.moau.moau.accounting.dues.dto.request;

import com.moau.moau.accounting.dues.domain.DuesStatus;
import jakarta.validation.constraints.NotNull;

public record DuesStatusUpdateRequestDto(
        @NotNull(message = "변경할 상태 값은 필수입니다.")
        DuesStatus status,

        @NotNull(message = "입금받을(또는 취소할) 은행 계좌 ID는 필수입니다.")
        Long bankAccountId,

        @NotNull(message = "카테고리 ID는 필수입니다.")
        Long categoryId,

        String memo
) {
}