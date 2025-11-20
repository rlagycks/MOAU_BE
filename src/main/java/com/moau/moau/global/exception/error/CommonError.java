package com.moau.moau.global.exception.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CommonError implements BaseError {

    // AUTH / JWT
    AUTH_HEADER_MISSING(HttpStatus.UNAUTHORIZED, "AUTH_HEADER_MISSING", "Authorization 헤더가 필요합니다."),
    AUTH_HEADER_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_HEADER_INVALID", "Authorization 헤더 형식이 올바르지 않습니다."),
    AUTH_SUBJECT_MISSING(HttpStatus.UNAUTHORIZED, "AUTH_SUBJECT_MISSING", "토큰에서 사용자 식별자를 읽을 수 없습니다."),
    AUTH_SUBJECT_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_SUBJECT_INVALID", "유효하지 않은 사용자 식별자입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "권한이 없습니다."),
    LOGOUT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "LOGOUT_UNAUTHORIZED", "로그아웃 요청이 인증되지 않았습니다."),

    // KAKAO LOGIN
    KAKAO_ACCESS_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "KAKAO_ACCESS_TOKEN_REQUIRED", "카카오 accessToken 값이 필요합니다."),
    KAKAO_USERINFO_NOT_FOUND(HttpStatus.UNAUTHORIZED, "KAKAO_USERINFO_NOT_FOUND", "카카오 사용자 정보를 조회할 수 없습니다."),
    KAKAO_EMAIL_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "KAKAO_EMAIL_NOT_PROVIDED", "카카오 계정에서 이메일 정보를 제공하지 않았습니다."),

    // REFRESH TOKEN
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

    // USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    USER_NICKNAME_EMPTY(HttpStatus.BAD_REQUEST, "USER_NICKNAME_EMPTY", "닉네임이 비어 있습니다."),

    // TEAM
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_NOT_FOUND", "팀을 찾을 수 없습니다."),
    TEAM_VIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "TEAM_VIEW_FORBIDDEN", "팀 조회 권한이 없습니다."),
    TEAM_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "TEAM_UPDATE_FORBIDDEN", "팀 수정 권한이 없습니다."),
    TEAM_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "TEAM_DELETE_FORBIDDEN", "팀 삭제 권한이 없습니다."),
    INVITE_CODE_INVALID(HttpStatus.BAD_REQUEST, "INVITE_CODE_INVALID", "유효하지 않은 초대 코드입니다."),

    // JOIN REQUEST
    JOIN_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "JOIN_REQUEST_NOT_FOUND", "가입 신청을 찾을 수 없습니다."),
    JOIN_REQUEST_ALREADY_HANDLED(HttpStatus.BAD_REQUEST, "JOIN_REQUEST_ALREADY_HANDLED", "이미 처리된 가입 신청입니다."),
    JOIN_REQUEST_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "JOIN_REQUEST_ALREADY_EXISTS", "이미 가입 신청이 접수된 상태입니다."),
    JOIN_REQUEST_FIELD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JOIN_REQUEST_FIELD_ERROR", "JoinRequest 필드를 설정하는 중 오류가 발생했습니다."),

    // TEAM MEMBER
    TEAM_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_MEMBER_NOT_FOUND", "해당 팀의 멤버가 아닙니다."),
    TEAM_MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "TEAM_MEMBER_ALREADY_EXISTS", "이미 이 팀의 멤버입니다."),
    TEAM_MEMBER_ALREADY_LEFT(HttpStatus.BAD_REQUEST, "TEAM_MEMBER_ALREADY_LEFT", "이미 팀을 나간 멤버입니다."),
    TEAM_MEMBER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "TEAM_MEMBER_NOT_ACTIVE", "비활성화된 멤버의 역할은 변경할 수 없습니다."),
    TEAM_MEMBER_FIELD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "TEAM_MEMBER_FIELD_ERROR", "TeamMember 필드를 설정하는 중 오류가 발생했습니다."),

    // TEAM OWNER & ROLE
    TEAM_ROLE_INVALID(HttpStatus.BAD_REQUEST, "TEAM_ROLE_INVALID", "유효하지 않은 역할입니다."),
    TEAM_OWNER_ROLE_CANNOT_BE_CHANGED(HttpStatus.FORBIDDEN, "TEAM_OWNER_ROLE_CANNOT_BE_CHANGED", "오너 역할은 변경할 수 없습니다."),
    TEAM_OWNER_CANNOT_BE_KICKED(HttpStatus.FORBIDDEN, "TEAM_OWNER_CANNOT_BE_KICKED", "팀 소유자는 강퇴할 수 없습니다."),
    TEAM_OWNER_CANNOT_LEAVE(HttpStatus.FORBIDDEN, "TEAM_OWNER_CANNOT_LEAVE", "대표는 팀을 나갈 수 없습니다."),
    TEAM_OWNER_TRANSFER_SELF(HttpStatus.BAD_REQUEST, "TEAM_OWNER_TRANSFER_SELF", "자기 자신에게는 오너를 양도할 수 없습니다."),
    TEAM_OWNER_TRANSFER_TARGET_NOT_MEMBER(HttpStatus.BAD_REQUEST, "TEAM_OWNER_TRANSFER_TARGET_NOT_MEMBER", "오너로 양도할 대상이 팀 멤버가 아닙니다."),
    TEAM_OWNER_TRANSFER_TARGET_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "TEAM_OWNER_TRANSFER_TARGET_NOT_ACTIVE", "비활성화된 멤버에게는 오너를 양도할 수 없습니다."),
    TEAM_OWNER_ALREADY(HttpStatus.BAD_REQUEST, "TEAM_OWNER_ALREADY", "이미 오너 권한을 가진 멤버입니다."),

    // ETC
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "그룹을 찾을 수 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public HttpStatus getHttpStatus() { return httpStatus; }
    @Override public String getCode()          { return code; }
    @Override public String getMessage()       { return message; }
}