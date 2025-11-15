package com.moau.moau.accounting.banking.dto.response;

public record BankAccountDto(
        Long bankAccountId,
        String alias,
        String bankName,
        String accountNumberMasked,
        String provider,
        boolean isConnected
) {
}