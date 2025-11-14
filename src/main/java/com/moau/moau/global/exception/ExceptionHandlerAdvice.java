package com.moau.moau.global.exception;

import com.moau.moau.global.exception.error.BaseError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        BaseError error = ex.getError();
        ErrorResponse response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, error.getHttpStatus());
    }
}