package com.moau.moau.accounting.banking.service;

import com.moau.moau.accounting.banking.domain.BankAccount;
import com.moau.moau.accounting.banking.domain.BankBalance;
import com.moau.moau.accounting.banking.util.BankCode;
import com.moau.moau.accounting.banking.dto.request.AccountRegisterRequestDto;
import com.moau.moau.accounting.banking.dto.response.BankAccountDto;
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

@Service
@RequiredArgsConstructor
@Transactional
public class BankingCommandService {

    private final BankAccountRepository bankAccountRepository;
    private final BankBalanceRepository bankBalanceRepository;
    private final TeamRepository teamRepository;


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

    private String maskAccountNumber(String number) {
        if (number == null || number.length() <= 4) return "********";
        return "********" + number.substring(number.length() - 4);
    }
}