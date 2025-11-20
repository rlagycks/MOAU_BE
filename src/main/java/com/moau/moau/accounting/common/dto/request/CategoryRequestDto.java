package com.moau.moau.accounting.common.dto.request;

import com.moau.moau.accounting.common.domain.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(
        @NotBlank @Size(max = 50)
        String name,

        @NotNull
        TransactionType type,

        @NotNull
        Boolean isActive
) {
}