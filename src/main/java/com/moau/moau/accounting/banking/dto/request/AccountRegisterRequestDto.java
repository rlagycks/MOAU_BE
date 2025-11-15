package com.moau.moau.accounting.banking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AccountRegisterRequestDto(
        @NotBlank @Size(max = 50)
        String alias,

        @NotBlank
        String bankCode,

        @NotBlank @Size(min = 10, max = 20)
        String accountNumber,

        @NotNull @PositiveOrZero
        Long initialBalanceCents
) {
}