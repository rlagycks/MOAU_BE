package com.moau.moau.accounting.receipt.dto.response;

import java.time.LocalDate;

public record AiReceiptResult(
        String merchantName,
        Long amount,
        LocalDate date,
        String paymentMethod
){}