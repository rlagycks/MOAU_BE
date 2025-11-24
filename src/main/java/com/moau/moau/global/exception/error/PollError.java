package com.moau.moau.global.exception.error;

import com.moau.moau.global.exception.error.BaseError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum PollError implements BaseError {
    POLL_NOT_FOUND(HttpStatus.NOT_FOUND, "POLL_NOT_FOUND", "투표 정보를 찾을 수 없습니다."),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "POLL_OPTION_NOT_FOUND", "존재하지 않는 투표 항목입니다."),
    CLOSED_POLL(HttpStatus.BAD_REQUEST, "POLL_CLOSED", "이미 마감된 투표입니다."),
    MULTIPLE_SELECTION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "POLL_MULTIPLE_NOT_ALLOWED", "중복 선택이 불가능한 투표입니다."),
    INVALID_OPTION(HttpStatus.BAD_REQUEST, "POLL_INVALID_OPTION", "해당 투표에 속하지 않은 항목입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public HttpStatus getHttpStatus() { return httpStatus; }
    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
}