package com.moau.moau.accounting.banking.repository;

import com.moau.moau.accounting.banking.domain.BankAccount;
import com.moau.moau.accounting.banking.domain.BankBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankBalanceRepository extends JpaRepository<BankBalance, Long> {

    Optional<BankBalance> findTopByBankAccountOrderByAsOfDesc(BankAccount bankAccount);
}