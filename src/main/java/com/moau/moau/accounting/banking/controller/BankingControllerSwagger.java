package com.moau.moau.accounting.banking.controller;

import com.moau.moau.accounting.banking.dto.request.AccountRegisterRequestDto;
import com.moau.moau.accounting.banking.dto.response.BalanceDto;
import com.moau.moau.accounting.banking.dto.response.BankAccountDto;
import com.moau.moau.accounting.banking.dto.response.BankDto;
import com.moau.moau.accounting.banking.dto.response.BankTransactionDto;
import com.moau.moau.global.payload.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "회계 - 뱅킹 API", description = "은행 목록 조회, 계좌 연동, 잔액 조회, 거래 내역 조회 API")
public interface BankingControllerSwagger {

    @Operation(summary = "은행 목록 조회",
            description = "연동 가능한 은행의 전체 목록을 조회합니다.")
    ResponseEntity<ResponseDto<List<BankDto>>> getBankList();

    @Operation(summary = "계좌 연동",
            description = "특정 팀(모임)에 은행 계좌를 연동(등록)합니다.")
    @Parameter(name = "teamId", description = "계좌를 연동할 팀 ID", example = "1")
    ResponseEntity<ResponseDto<BankAccountDto>> registerAccount(
            @PathVariable Long teamId,
            @Valid @RequestBody AccountRegisterRequestDto requestDto
    );

    @Operation(summary = "계좌 잔액 조회",
            description = "연동된 계좌의 현재 잔액을 조회합니다.")
    @Parameter(name = "teamId", description = "조회할 팀 ID", example = "1")
    ResponseEntity<ResponseDto<BalanceDto>> getAccountBalance(
            @PathVariable Long teamId
    );

    @Operation(summary = "계좌 거래내역 조회 (페이징)",
            description = "연동된 계좌의 거래 내역을 기간별로 조회합니다.")
    @Parameter(name = "teamId", description = "조회할 팀 ID", example = "1")
    @Parameter(name = "startDate", description = "조회 시작일 (YYYY-MM-DD)", example = "2025-11-01")
    @Parameter(name = "endDate", description = "조회 종료일 (YYYY-MM-DD)", example = "2025-11-30")
    @Parameter(name = "pageable", hidden = true) // Swagger에서 Pageable 파라미터를 숨기고
    @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0") // page를 수동으로 노출
    @Parameter(name = "size", description = "페이지 당 항목 수", example = "10") // size를 수동으로 노출
    @Parameter(name = "sort", description = "정렬 (예: txnDate,desc)", example = "txnDate,desc") // sort를 수동으로 노출
    ResponseEntity<ResponseDto<Page<BankTransactionDto>>> getAccountTransactions(
            @PathVariable Long teamId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable // ⬅️ 인터페이스에는 @PageableDefault를 뺍니다.
    );
}