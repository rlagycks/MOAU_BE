package com.moau.moau.accounting.banking.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bank_balances")
public class BankBalance extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Column(name = "balance_cents", nullable = false)
    private Long balanceCents;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "as_of", nullable = false)
    private Instant asOf;

    @Builder
    public BankBalance(BankAccount bankAccount, Long balanceCents, String currency, Instant asOf) {
        this.bankAccount = bankAccount;
        this.balanceCents = balanceCents;
        this.currency = currency;
        this.asOf = asOf;
    }
}