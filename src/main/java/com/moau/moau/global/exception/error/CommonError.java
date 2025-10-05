package com.moau.moau.global.exception.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CommonError implements BaseError {
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "그룹을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public HttpStatus getHttpStatus() { return httpStatus; }
    @Override public String getCode()          { return code; }
    @Override public String getMessage()       { return message; }
}
