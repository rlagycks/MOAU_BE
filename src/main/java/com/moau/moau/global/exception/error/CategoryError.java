package com.moau.moau.global.exception.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CategoryError implements BaseError {

    NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "해당 카테고리를 찾을 수 없습니다."),
    NAME_DUPLICATED(HttpStatus.CONFLICT, "CATEGORY_NAME_DUPLICATED", "해당 팀에 이미 동일한 이름의 카테고리가 존재합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public HttpStatus getHttpStatus() { return httpStatus; }
    @Override public String getCode()          { return code; }
    @Override public String getMessage()       { return message; }
}