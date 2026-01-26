package com.moau.moau.accounting.banking.repository;

import com.moau.moau.accounting.banking.domain.BankAccount;
import com.moau.moau.accounting.banking.domain.BankBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankBalanceRepository extends JpaRepository<BankBalance, Long> {

    Optional<BankBalance> findTopByBankAccountOrderByAsOfDesc(BankAccount bankAccount);

    // Race Condition 방지: Pessimistic Lock으로 최신 잔액 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BankBalance b WHERE b.bankAccount = :account ORDER BY b.asOf DESC LIMIT 1")
    Optional<BankBalance> findLatestWithLock(@Param("account") BankAccount account);
}