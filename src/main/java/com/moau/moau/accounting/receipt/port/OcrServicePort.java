package com.moau.moau.accounting.receipt.port;

import org.springframework.scheduling.annotation.Async;

public interface OcrServicePort {
    // 비동기 OCR 요청
    @Async
    void requestOcr(Long receiptId);
}