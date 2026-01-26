package com.moau.moau.global.exception.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DuesError implements BaseError {

    CYCLE_NOT_FOUND(HttpStatus.NOT_FOUND, "DUES_CYCLE_NOT_FOUND", "회비 납부 주기를 찾을 수 없습니다."),
    MEMBER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "DUES_MEMBER_STATUS_NOT_FOUND", "해당 멤버의 납부 정보를 찾을 수 없습니다."),
    INVALID_CYCLE_DATE(HttpStatus.BAD_REQUEST, "INVALID_CYCLE_DATE", "유효하지 않은 회비 주기 날짜입니다."),
    TEAM_SETTING_NOT_FOUND(HttpStatus.BAD_REQUEST, "TEAM_SETTING_NOT_FOUND", "팀의 회비 설정(주기/금액)이 되어있지 않습니다."),
    ALREADY_PAID(HttpStatus.CONFLICT, "ALREADY_PAID", "이미 납부된 회비입니다."),
    ALREADY_UNPAID(HttpStatus.CONFLICT, "ALREADY_UNPAID", "이미 미납 상태입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public HttpStatus getHttpStatus() { return httpStatus; }
    @Override public String getCode()          { return code; }
    @Override public String getMessage()       { return message; }
}