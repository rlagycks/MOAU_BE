package com.moau.moau.accounting.banking.dto.response;

import java.time.Instant;

public record BalanceDto(
        Long balanceCents,
        String currency,
        Instant asOf
) {
}