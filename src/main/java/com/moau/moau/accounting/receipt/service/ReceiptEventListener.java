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

        ocrServicePort.requestOcr(event.receiptId());
    }
}