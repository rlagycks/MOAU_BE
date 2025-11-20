package com.moau.moau.team.dto.request;

import com.moau.moau.team.domain.DuesPeriod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record TeamUpdateRequest(
        @NotBlank(message = "팀 이름은 필수입니다.")
        @Size(max = 20, message = "팀 이름은 20자 이내여야 합니다.")
        String name,

        @Size(max = 100, message = "팀 설명은 100자 이내여야 합니다.")
        String description,

        @NotNull(message = "회비 주기는 필수입니다. (없음=NONE)")
        DuesPeriod duesPeriod,

        @NotNull(message = "회비 금액은 필수입니다.")
        @PositiveOrZero(message = "회비 금액은 0원 이상이어야 합니다.")
        Long duesAmount
) {
}