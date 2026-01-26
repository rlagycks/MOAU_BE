package com.moau.moau.accounting.receipt.service;

import com.moau.moau.accounting.receipt.port.OcrServicePort;
import com.moau.moau.accounting.receipt.domain.ReceiptCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventListener {

    private final OcrServicePort ocrServicePort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReceiptCreatedEvent(ReceiptCreatedEvent event) {
        log.info("[Event] Receipt created, triggering OCR. receiptId: {}", event.receiptId());

        try {
            ocrServicePort.requestOcr(event.receiptId());
            log.info("[Event] OCR processing completed for receiptId: {}", event.receiptId());
        } catch (Exception e) {
            log.error("[Event] OCR processing failed for receiptId: {}. Error: {}",
                    event.receiptId(), e.getMessage(), e);
            // OCR 실패 시에도 예외를 던지지 않아 트랜잭션 롤백 방지
        }
    }
}