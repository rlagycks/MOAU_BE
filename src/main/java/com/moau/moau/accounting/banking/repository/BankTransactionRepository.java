package com.moau.moau.accounting.banking.repository;

import com.moau.moau.accounting.banking.domain.BankAccount;
import com.moau.moau.accounting.banking.domain.BankTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    Page<BankTransaction> findByBankAccountAndTxnDateBetween(
            BankAccount bankAccount,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    Page<BankTransaction> findByBankAccount(BankAccount bankAccount, Pageable pageable);
}