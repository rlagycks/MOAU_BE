package com.moau.moau.accounting.banking.service;

import com.moau.moau.accounting.banking.domain.BankAccount;
import com.moau.moau.accounting.banking.domain.BankBalance;
import com.moau.moau.accounting.banking.domain.BankTransaction;
import com.moau.moau.accounting.banking.repository.BankTransactionRepository;
import com.moau.moau.accounting.banking.util.BankCode;
import com.moau.moau.accounting.banking.dto.request.AccountRegisterRequestDto;
import com.moau.moau.accounting.banking.dto.response.BankAccountDto;
import com.moau.moau.global.exception.error.CategoryError;
import com.moau.moau.accounting.common.repository.CategoryRepository;
import com.moau.moau.global.exception.error.BankingError;
import com.moau.moau.accounting.banking.repository.BankAccountRepository;
import com.moau.moau.accounting.banking.repository.BankBalanceRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.team.domain.TeamMemberRole;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class BankingCommandService {

    private final BankAccountRepository bankAccountRepository;
    private final BankBalanceRepository bankBalanceRepository;
    private final TeamRepository teamRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final CategoryRepository categoryRepository;
    private final TeamMemberRepository teamMemberRepository;

    public BankAccountDto registerAccount(Long teamId, AccountRegisterRequestDto dto) {
        teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        Long currentUserId = SecurityUtil.getCurrentUserId();
        requireMemberWithRole(teamId, currentUserId, TeamMemberRole.ADMIN);

        bankAccountRepository.findFirstByTeamId(teamId).ifPresent(acc -> {
            throw new BusinessException(BankingError.ACCOUNT_ALREADY_REGISTERED);
        });

        BankCode bankCode = BankCode.getByCode(dto.bankCode());
        String maskedNumber = maskAccountNumber(dto.accountNumber());

        BankAccount newAccount = BankAccount.builder()
                .teamId(teamId)
                .alias(dto.alias())
                .bankCode(bankCode.getCode())
                .bankName(bankCode.getName())
                .accountNumberMasked(maskedNumber)
                .provider("DUMMY")
                .isConnected(true)
                .build();
        bankAccountRepository.save(newAccount);

        BankBalance initialBalance = BankBalance.builder()
                .bankAccount(newAccount)
                .balanceCents(dto.initialBalanceCents())
                .currency("KRW")
                .asOf(Instant.now())
                .build();
        bankBalanceRepository.save(initialBalance);

        return new BankAccountDto(
                newAccount.getId(),
                newAccount.getAlias(),
                newAccount.getBankName(),
                newAccount.getAccountNumberMasked(),
                newAccount.getProvider(),
                newAccount.isConnected()
        );
    }

    @Transactional
    public void recordExpense(Long teamId, Long bankAccountId, Long amountCents, Long categoryId,
                              String description, LocalDate transactionDate, Long reviewId) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        requireMemberWithRole(teamId, currentUserId, TeamMemberRole.ADMIN);

        BankAccount account = bankAccountRepository.findByIdAndTeamId(bankAccountId, teamId)
                .orElseThrow(() -> new BusinessException(BankingError.ACCOUNT_NOT_FOUND));

        teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        categoryRepository.findByIdAndTeamId(categoryId, teamId)
                .orElseThrow(() -> new BusinessException(CategoryError.NOT_FOUND));

        BankTransaction expense = BankTransaction.builder()
                .bankAccount(account)
                .txnDate(transactionDate)
                .amountCents(amountCents * -1)
                .description(description)
                .build();
        bankTransactionRepository.save(expense);

        // 4. 잔액 차감
        updateBalance(account, amountCents * -1);
    }

    @Transactional
    public void recordIncome(Long teamId, Long bankAccountId, Long amountCents, Long categoryId,
                             String description, LocalDate transactionDate) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        requireMemberWithRole(teamId, currentUserId, TeamMemberRole.ADMIN);

        // 1. 계좌 소유권 검증
        BankAccount account = bankAccountRepository.findByIdAndTeamId(bankAccountId, teamId)
                .orElseThrow(() -> new BusinessException(BankingError.ACCOUNT_NOT_FOUND));

        teamRepository.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new BusinessException(CommonError.TEAM_NOT_FOUND));

        // 2. 카테고리 검증
        categoryRepository.findByIdAndTeamId(categoryId, teamId)
                .orElseThrow(() -> new BusinessException(CategoryError.NOT_FOUND));

        // 3. 수입 내역 기록 (양수 저장)
        BankTransaction income = BankTransaction.builder()
                .bankAccount(account)
                .txnDate(transactionDate)
                .amountCents(amountCents) // 양수
                .description(description)
                .build();
        bankTransactionRepository.save(income);

        // 4. 잔액 증가
        updateBalance(account, amountCents);
    }

    // (공통) 잔액 업데이트 로직 분리
    private void updateBalance(BankAccount account, Long amountDelta) {
        BankBalance latestBalance = bankBalanceRepository.findTopByBankAccountOrderByAsOfDesc(account)
                .orElseThrow(() -> new BusinessException(BankingError.ACCOUNT_NOT_FOUND, "계좌의 잔액 정보가 없습니다."));

        BankBalance updatedBalance = BankBalance.builder()
                .bankAccount(account)
                .balanceCents(latestBalance.getBalanceCents() + amountDelta) // (기존 잔액 + 변동액)
                .currency("KRW")
                .asOf(Instant.now())
                .build();
        bankBalanceRepository.save(updatedBalance);
    }

    private String maskAccountNumber(String number) {
        if (number == null || number.length() < 4) {
            return "****";
        }
        int visible = 4;
        String tail = number.substring(number.length() - visible);
        int maskedLength = Math.max(0, number.length() - visible);
        return "*".repeat(maskedLength) + tail;
    }

    private void requireMemberWithRole(Long teamId, Long userId, TeamMemberRole requiredRole) {
        var member = teamMemberRepository.findActiveMember(teamId, userId)
                .orElseThrow(() -> new BusinessException(CommonError.ACCESS_DENIED));
        if (!member.getRole().isAtLeast(requiredRole)) {
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }
    }
}
