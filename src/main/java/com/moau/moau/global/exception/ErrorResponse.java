package com.moau.moau.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,          // HTTP status code
        String code,         // 비즈니스 에러 코드
        String message,      // 사용자 메시지
        String path      // 요청 URI
) {
    public static ErrorResponse of(HttpStatus status, String code, String message,
                                   String path) {
        return new ErrorResponse(Instant.now(), status.value(), code, message, path);
    }
}