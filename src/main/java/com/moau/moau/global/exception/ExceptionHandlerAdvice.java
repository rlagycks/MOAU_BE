package com.moau.moau.global.exception;

import com.moau.moau.global.exception.error.BaseError;
import com.moau.moau.global.exception.error.CommonError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        BaseError error = ex.getError();
        ErrorResponse response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI(), ex.getDetail());
        return new ResponseEntity<>(response, error.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        var details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        var error = CommonError.BAD_REQUEST;
        var response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI(), details);
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        var details = ex.getConstraintViolations().stream()
                .map(v -> new FieldErrorDetail(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        var error = CommonError.BAD_REQUEST;
        var response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI(), details);
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, ServletRequestBindingException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        var error = CommonError.BAD_REQUEST;
        var response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        var error = CommonError.AUTH_HEADER_INVALID;
        var response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        var error = CommonError.ACCESS_DENIED;
        var response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        var error = CommonError.BAD_REQUEST;
        var response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponse(WebClientResponseException ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        var error = CommonError.EXTERNAL_API_ERROR;
        var detail = new ExternalApiErrorDetail(ex.getStatusCode().value(), ex.getResponseBodyAsString());
        var response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI(), detail);
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        var error = CommonError.DATA_INTEGRITY_VIOLATION;
        var response = ErrorResponse.of(error.getHttpStatus(), error.getCode(), error.getMessage(), request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        var error = CommonError.INTERNAL_SERVER_ERROR;
        var response = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, error.getCode(), error.getMessage(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    public record FieldErrorDetail(String field, String message) {}

    public record ExternalApiErrorDetail(int status, String responseBody) {}

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
}
