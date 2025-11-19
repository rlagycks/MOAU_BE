package com.moau.moau.accounting.banking.service;

import com.moau.moau.accounting.banking.domain.BankAccount;
import com.moau.moau.accounting.banking.domain.BankBalance;
import com.moau.moau.accounting.banking.domain.BankTransaction;
import com.moau.moau.accounting.banking.repository.BankTransactionRepository;
import com.moau.moau.accounting.banking.util.BankCode;
import com.moau.moau.accounting.banking.dto.request.AccountRegisterRequestDto;
import com.moau.moau.accounting.banking.dto.response.BankAccountDto;
import com.moau.moau.accounting.common.domain.Category;
import com.moau.moau.global.exception.error.CategoryError;
import com.moau.moau.accounting.common.repository.CategoryRepository;
import com.moau.moau.global.exception.error.BankingError;
import com.moau.moau.accounting.banking.repository.BankAccountRepository;
import com.moau.moau.accounting.banking.repository.BankBalanceRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
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

    public BankAccountDto registerAccount(Long teamId, AccountRegisterRequestDto dto) {
        if (!teamRepository.existsById(teamId)) {
            throw new BusinessException(CommonError.TEAM_NOT_FOUND);
        }
        if (bankAccountRepository.existsByTeamId(teamId)) {
            throw new BusinessException(BankingError.ACCOUNT_ALREADY_REGISTERED);
        }

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

        // 1. 계좌, 카테고리, 팀 유효성 검증
        BankAccount account = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new BusinessException(BankingError.ACCOUNT_NOT_FOUND));

        if (!account.getTeamId().equals(teamId)) {
            throw new BusinessException(BankingError.ACCOUNT_NOT_FOUND);
        }

        Category category = categoryRepository.findByIdAndTeamId(categoryId, teamId)
                .orElseThrow(() -> new BusinessException(CategoryError.NOT_FOUND));

        // 2. BANK_TRANSACTIONS 에 지출(음수) 내역 INSERT
        BankTransaction expense = BankTransaction.builder()
                .bankAccount(account)
                .txnDate(transactionDate)
                .amountCents(amountCents * -1) // 지출이므로 음수로 변환
                .description(description)
                .build();
        bankTransactionRepository.save(expense);

        // 3. BANK_BALANCES 의 최신 잔액 UPDATE (차감)
        BankBalance latestBalance = bankBalanceRepository.findTopByBankAccountOrderByAsOfDesc(account)
                .orElseThrow(() -> new BusinessException(BankingError.ACCOUNT_NOT_FOUND, "계좌의 잔액 정보가 없습니다."));

        BankBalance updatedBalance = BankBalance.builder()
                .bankAccount(account)
                .balanceCents(latestBalance.getBalanceCents() - amountCents) // 잔액 차감
                .currency("KRW")
                .asOf(Instant.now())
                .build();
        bankBalanceRepository.save(updatedBalance);
    }

    private String maskAccountNumber(String number) {
        if (number == null || number.length() <= 4) return "********";
        return "********" + number.substring(number.length() - 4);
    }
}