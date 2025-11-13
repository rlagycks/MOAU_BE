package com.moau.moau.global.exception.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CommonError implements BaseError {
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "그룹을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "권한이 없습니다."),

    // [ 이 부분을 추가해주세요]
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_NOT_FOUND", "팀을 찾을 수 없습니다."),

    LOGOUT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "LOGOUT_UNAUTHORIZED", "로그아웃 요청이 인증되지 않았습니다."),

    REFRESH_TOKEN_INVALID_TYPE(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_INVALID_TYPE", "리프레시 토큰이 아닙니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_INVALID", "리프레시 토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_EXPIRED", "리프레시 토큰이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_NOT_FOUND", "리프레시 토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_MISMATCH", "리프레시 토큰이 일치하지 않습니다."),
    TOKEN_HASH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_HASH_ERROR", "토큰 해시 계산 중 오류가 발생했습니다."),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "유효하지 않은 토큰입니다."),

    JWT_SECRET_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR, "JWT_SECRET_EMPTY", "JWT 시크릿 설정 값이 비어 있습니다."),
    JWT_SECRET_SHA256_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JWT_SECRET_SHA256_FAILED", "JWT 시크릿 해시 처리 중 오류가 발생했습니다."),

    KAKAO_USERINFO_NOT_FOUND(HttpStatus.UNAUTHORIZED, "KAKAO_USERINFO_NOT_FOUND", "카카오 사용자 정보를 조회할 수 없습니다."),
    KAKAO_EMAIL_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "KAKAO_EMAIL_NOT_PROVIDED", "카카오 계정에서 이메일 정보를 제공하지 않았습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),

    KAKAO_ACCESS_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "KAKAO_ACCESS_TOKEN_REQUIRED", "카카오 accessToken 값이 필요합니다."),

    AUTH_HEADER_MISSING(HttpStatus.UNAUTHORIZED, "AUTH_HEADER_MISSING", "Authorization 헤더가 필요합니다."),
    AUTH_HEADER_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_HEADER_INVALID", "Authorization 헤더 형식이 올바르지 않습니다."),
    AUTH_SUBJECT_MISSING(HttpStatus.UNAUTHORIZED, "AUTH_SUBJECT_MISSING", "토큰에서 사용자 식별자를 읽을 수 없습니다."),
    AUTH_SUBJECT_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_SUBJECT_INVALID", "유효하지 않은 사용자 식별자입니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public HttpStatus getHttpStatus() { return httpStatus; }
    @Override public String getCode()          { return code; }
    @Override public String getMessage()       { return message; }
}