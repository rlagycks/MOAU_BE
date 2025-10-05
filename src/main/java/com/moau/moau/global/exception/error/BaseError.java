package com.moau.moau.global.exception.error;

import org.springframework.http.HttpStatus;

public interface BaseError {
    HttpStatus getHttpStatus(); // 상태코드
    String getCode();     // 비지니스 에러 유형 ex) "GROUP_NOT_FOUND"
    String getMessage();  // 기본 메세지
}