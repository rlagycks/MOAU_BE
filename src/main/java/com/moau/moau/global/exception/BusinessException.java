package com.moau.moau.global.exception;

import com.moau.moau.global.exception.error.BaseError;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BaseError error;   // 상태/코드/기본 메시지
    private final String detail;

    public BusinessException(BaseError error) {
        super(error.getMessage());
        this.error = error;
        this.detail = null;
    }

    public BusinessException(BaseError error, String detail) {
        super(error.getMessage());
        this.error = error;
        this.detail = detail;
    }

    public BusinessException(BaseError error, String detail, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
        this.detail = detail;
    }
}