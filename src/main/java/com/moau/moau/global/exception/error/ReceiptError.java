package com.moau.moau.global.exception.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReceiptError implements BaseError {

    RECEIPT_NOT_FOUND(HttpStatus.NOT_FOUND, "RECEIPT_NOT_FOUND", "영수증을 찾을 수 없습니다."),
    RECEIPT_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "RECEIPT_REVIEW_NOT_FOUND", "영수증 승인 요청을 찾을 수 없습니다."),
    ALREADY_REVIEW_REQUESTED(HttpStatus.CONFLICT, "ALREADY_REVIEW_REQUESTED", "이미 승인 요청된 영수증입니다."),
    ALREADY_REVIEW_COMPLETED(HttpStatus.CONFLICT, "ALREADY_REVIEW_COMPLETED", "이미 처리(승인/반려)가 완료된 요청입니다."),
    INVALID_REVIEW_STATUS(HttpStatus.BAD_REQUEST, "INVALID_REVIEW_STATUS", "잘못된 승인 상태입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public HttpStatus getHttpStatus() { return httpStatus; }
    @Override public String getCode()          { return code; }
    @Override public String getMessage()       { return message; }
}