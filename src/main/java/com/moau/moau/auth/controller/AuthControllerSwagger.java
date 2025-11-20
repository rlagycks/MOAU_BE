package com.moau.moau.auth.controller;

import com.moau.moau.auth.dto.request.CodeExchangeRequest;
import com.moau.moau.auth.dto.response.CodeExchangeResponse;
import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.token.dto.request.LogoutRequest;
import com.moau.moau.token.dto.request.RefreshRequest;
import com.moau.moau.token.dto.response.RefreshResponse;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.*;

@Tag(
        name = "🛡️ Auth",
        description = "인증 및 인가(Access/Refresh, 로그아웃, 카카오 인증, 테스트 로그인) API"
)
public class AuthControllerSwagger {

    // ============================================================
    // 1) 카카오 교환
    // ============================================================
    @Inherited
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "카카오 AccessToken 교환",
            description = """
                    카카오 SDK 로그인 후 받은 accessToken을 서버로 전달하면,
                    서버는 카카오 API로 사용자 정보를 검증하고,
                    Access/Refresh JWT를 발급합니다.
                    """,
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CodeExchangeRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "JWT 발급 성공",
                            content = @Content(schema = @Schema(implementation = CodeExchangeResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "accessToken 누락/파싱 실패",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public @interface ExchangeCode {}



    // ============================================================
    // 2) Refresh Token 재발급
    // ============================================================
    @Inherited
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "리프레시 토큰 재발급",
            description = "RefreshToken을 검증해 Access/Refresh 토큰을 재발급합니다.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "재발급 성공",
                            content = @Content(schema = @Schema(implementation = RefreshResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 RefreshToken",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public @interface Refresh {}



    // ============================================================
    // 3) 로그아웃
    // ============================================================
    @Inherited
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "로그아웃",
            description = "전달받은 RefreshToken을 즉시 폐기하여 로그아웃 처리합니다.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = LogoutRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "로그아웃 성공")
            }
    )
    public @interface Logout {}



    // ============================================================
    // 4) 테스트 로그인
    // ============================================================
    @Inherited
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "(DEV) 테스트 로그인",
            description = """
                    개발 환경 전용 API.
                    임시 유저 생성/조회하여 Access/Refresh 토큰을 발급합니다.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "테스트 로그인 성공",
                            content = @Content(schema = @Schema(implementation = CodeExchangeResponse.class))
                    )
            }
    )
    public @interface TestLogin {}
}
