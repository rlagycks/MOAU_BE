package com.moau.moau.accounting.banking.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "bank_accounts")
public class BankAccount extends BaseId {

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private String alias;

    @Column(name = "bank_code", nullable = false, length = 3)
    private String bankCode;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "account_number_masked", nullable = false)
    private String accountNumberMasked;

    @Column(nullable = false, length = 50)
    private String provider; // "DUMMY", "OPEN_BANKING_KR"

    @Column(name = "is_connected", nullable = false)
    private boolean isConnected;

    @Builder
    public BankAccount(Long teamId, String alias, String bankCode, String bankName, String accountNumberMasked, String provider, boolean isConnected) {
        this.teamId = teamId;
        this.alias = alias;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.accountNumberMasked = accountNumberMasked;
        this.provider = provider;
        this.isConnected = isConnected;
    }
}