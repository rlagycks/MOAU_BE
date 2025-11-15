package com.moau.moau.accounting.banking.controller;

import com.moau.moau.accounting.banking.dto.request.AccountRegisterRequestDto;
import com.moau.moau.accounting.banking.dto.response.BalanceDto;
import com.moau.moau.accounting.banking.dto.response.BankAccountDto;
import com.moau.moau.accounting.banking.dto.response.BankDto;
import com.moau.moau.accounting.banking.dto.response.BankTransactionDto;
import com.moau.moau.accounting.banking.service.BankingCommandService;
import com.moau.moau.accounting.banking.service.BankingQueryService;
import com.moau.moau.global.payload.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BankingController {

    private final BankingCommandService bankingCommandService;
    private final BankingQueryService bankingQueryService;

    @GetMapping("/banks")
    public ResponseEntity<ResponseDto<List<BankDto>>> getBankList() {
        return ResponseEntity.ok(ResponseDto.data(bankingQueryService.getBankList()));
    }

    @PostMapping("/teams/{teamId}/accounting/banking/account")
    public ResponseEntity<ResponseDto<BankAccountDto>> registerAccount(
            @PathVariable Long teamId,
            @Valid @RequestBody AccountRegisterRequestDto requestDto
    ) {
        BankAccountDto responseDto = bankingCommandService.registerAccount(teamId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.data(responseDto));
    }

    @GetMapping("/teams/{teamId}/accounting/banking/balance")
    public ResponseEntity<ResponseDto<BalanceDto>> getAccountBalance(
            @PathVariable Long teamId
    ) {
        return ResponseEntity.ok(ResponseDto.data(bankingQueryService.getAccountBalance(teamId)));
    }

    @GetMapping("/teams/{teamId}/accounting/banking/transactions")
    public ResponseEntity<ResponseDto<Page<BankTransactionDto>>> getAccountTransactions(
            @PathVariable Long teamId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(sort = "txnDate", direction = Sort.Direction.DESC, size = 10) Pageable pageable
    ) {
        Page<BankTransactionDto> responseDto = bankingQueryService.getAccountTransactions(teamId, startDate, endDate, pageable);
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }
}