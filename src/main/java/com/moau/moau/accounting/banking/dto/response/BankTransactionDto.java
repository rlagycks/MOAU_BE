package com.moau.moau.accounting.banking.dto.response;

import java.time.LocalDate;

public record BankTransactionDto(
        Long bankTransactionId,
        LocalDate txnDate,
        Long amountCents,
        String description
) {
}