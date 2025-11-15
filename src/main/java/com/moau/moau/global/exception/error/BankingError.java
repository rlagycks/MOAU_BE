package com.moau.moau.global.exception.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BankingError implements BaseError {

    BANK_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "BANK_CODE_NOT_FOUND", "존재하지 않는 은행 코드입니다."),
    ACCOUNT_ALREADY_REGISTERED(HttpStatus.CONFLICT, "ACCOUNT_ALREADY_REGISTERED", "해당 팀에 이미 등록된 계좌가 있습니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND", "등록된 계좌 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public HttpStatus getHttpStatus() { return httpStatus; }
    @Override public String getCode()          { return code; }
    @Override public String getMessage()       { return message; }
}