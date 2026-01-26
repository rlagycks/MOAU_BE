package com.moau.moau.accounting.dues.service;

import com.moau.moau.accounting.banking.service.BankingCommandService;
import com.moau.moau.accounting.dues.domain.DuesStatus;
import com.moau.moau.accounting.dues.domain.DuesStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DuesEventListener {

    private final BankingCommandService bankingCommandService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDuesStatusChanged(DuesStatusChangedEvent event) {
        log.info("[Event] Dues status changed. cycleId: {}, userId: {}, {} -> {}",
                event.cycleId(), event.userId(), event.previousStatus(), event.newStatus());

        // 감사 추적 (Audit Trail) - 로그로 기록
        log.info("[Audit] Dues status changed by user {}. Cycle: {}, Target User: {}, Amount: {}, {} -> {}",
                event.changedBy(), event.cycleName(), event.userId(), event.amountCents(),
                event.previousStatus(), event.newStatus());
    }
}
