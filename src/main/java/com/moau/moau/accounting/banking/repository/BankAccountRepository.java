package com.moau.moau.accounting.banking.repository;

import com.moau.moau.accounting.banking.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByTeamId(Long teamId);

    boolean existsByTeamId(Long teamId);
}