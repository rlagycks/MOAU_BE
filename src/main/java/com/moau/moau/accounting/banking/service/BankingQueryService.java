package com.moau.moau.accounting.banking.service;

import com.moau.moau.accounting.banking.domain.BankAccount;
import com.moau.moau.accounting.banking.domain.BankBalance;
import com.moau.moau.accounting.banking.util.BankCode;
import com.moau.moau.accounting.banking.domain.BankTransaction;
import com.moau.moau.accounting.banking.dto.response.BalanceDto;
import com.moau.moau.accounting.banking.dto.response.BankDto;
import com.moau.moau.accounting.banking.dto.response.BankTransactionDto;
import com.moau.moau.global.exception.error.BankingError;
import com.moau.moau.accounting.banking.repository.BankAccountRepository;
import com.moau.moau.accounting.banking.repository.BankBalanceRepository;
import com.moau.moau.accounting.banking.repository.BankTransactionRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.team.domain.TeamMemberRole;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankingQueryService {

    private final BankAccountRepository bankAccountRepository;
    private final BankBalanceRepository bankBalanceRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public List<BankDto> getBankList() {
        return Arrays.stream(BankCode.values())
                .map(bankCode -> new BankDto(bankCode.getCode(), bankCode.getName()))
                .collect(Collectors.toList());
    }

    public BalanceDto getAccountBalance(Long teamId) {
        Long userId = SecurityUtil.getCurrentUserId();
        requireMemberWithRole(teamId, userId, TeamMemberRole.MEMBER);

        BankAccount account = findAccountByTeamId(teamId);
        BankBalance latestBalance = bankBalanceRepository.findTopByBankAccountOrderByAsOfDesc(account)
                .orElseThrow(() -> new BusinessException(BankingError.ACCOUNT_NOT_FOUND));

        return new BalanceDto(
                latestBalance.getBalanceCents(),
                latestBalance.getCurrency(),
                latestBalance.getAsOf()
        );
    }

    public Page<BankTransactionDto> getAccountTransactions(Long teamId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        requireMemberWithRole(teamId, userId, TeamMemberRole.MEMBER);

        BankAccount account = findAccountByTeamId(teamId);
        LocalDate start = (startDate != null) ? startDate : LocalDate.of(1900, 1, 1);
        LocalDate end = (endDate != null) ? endDate : LocalDate.now();

        Page<BankTransaction> transactionPage = bankTransactionRepository
                .findByBankAccountAndTxnDateBetween(account, start, end, pageable);

        return transactionPage.map(this::mapToBankTransactionDto);
    }

    private BankAccount findAccountByTeamId(Long teamId) {
        teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        return bankAccountRepository.findFirstByTeamId(teamId)
                .orElseThrow(() -> new BusinessException(BankingError.ACCOUNT_NOT_FOUND));
    }

    private BankTransactionDto mapToBankTransactionDto(BankTransaction tx) {
        return new BankTransactionDto(
                tx.getId(),
                tx.getTxnDate(),
                tx.getAmountCents(),
                tx.getDescription()
        );
    }

    private void requireMemberWithRole(Long teamId, Long userId, TeamMemberRole requiredRole) {
        var member = teamMemberRepository.findActiveMember(teamId, userId)
                .orElseThrow(() -> new BusinessException(CommonError.ACCESS_DENIED));
        if (!member.getRole().isAtLeast(requiredRole)) {
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }
    }
}
