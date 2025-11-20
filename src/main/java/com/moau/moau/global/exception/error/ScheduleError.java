package com.moau.moau.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleError implements BaseError {

    // 404: 일정을 찾을 수 없음
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_NOT_FOUND", "일정을 찾을 수 없습니다."),

    // 400: 종료 시간이 시작 시간보다 빠름 (기간 유효성 검사)
    INVALID_SCHEDULE_PERIOD(HttpStatus.BAD_REQUEST, "INVALID_SCHEDULE_PERIOD", "종료 시간은 시작 시간보다 이후여야 합니다."),

    // 400: 제목 길이 초과 or 빈 값 (최대 50자)
    INVALID_TITLE_LENGTH(HttpStatus.BAD_REQUEST, "INVALID_TITLE_LENGTH", "일정 제목은 1자 이상 50자 이하여야 합니다."),

    // 설명 길이 초과 (최대 500자)
    INVALID_DESCRIPTION_LENGTH(HttpStatus.BAD_REQUEST, "INVALID_DESCRIPTION_LENGTH", "일정 설명은 500자를 초과할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}