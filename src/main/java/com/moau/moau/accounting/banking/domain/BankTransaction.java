package com.moau.moau.accounting.banking.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bank_transactions")
public class BankTransaction extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Column(name = "txn_date", nullable = false)
    private LocalDate txnDate;

    @Column(name = "amount_cents", nullable = false)
    private Long amountCents; // (수입: 양수, 지출: 음수)

    @Column(nullable = false)
    private String description;

    @Builder
    public BankTransaction(BankAccount bankAccount, LocalDate txnDate, Long amountCents, String description) {
        this.bankAccount = bankAccount;
        this.txnDate = txnDate;
        this.amountCents = amountCents;
        this.description = description;
    }
}