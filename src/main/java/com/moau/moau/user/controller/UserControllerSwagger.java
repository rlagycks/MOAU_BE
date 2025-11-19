package com.moau.moau.user.controller;

import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.user.dto.request.UserUpdateRequest;
import com.moau.moau.user.dto.response.UserMeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "🙍 User", description = "내 정보 조회 / 수정 / 삭제 API")
public interface UserControllerSwagger {

    // =========================
    // 1) 현재 사용자 정보 조회
    // =========================
    @Operation(
            summary = "내 정보 조회",
            description = "JWT의 subject(userId)를 기반으로 현재 로그인된 사용자 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserMeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/me")
    ResponseEntity<UserMeResponse> me(
            @RequestHeader("Authorization") String authorization
    );

    // =========================
    // 2) 닉네임 수정 (닉네임만 변경)
    // =========================
    @Operation(
            summary = "내 닉네임 수정",
            description = "사용자의 닉네임만 수정합니다. 다른 정보는 변경되지 않습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공 — 수정된 닉네임을 포함한 전체 사용자 정보 반환",
                    content = @Content(schema = @Schema(implementation = UserMeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("/me")
    ResponseEntity<UserMeResponse> updateMe(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid UserUpdateRequest request
    );

    // =========================
    // 3) 계정 삭제 (Soft Delete)
    // =========================
    @Operation(
            summary = "내 계정 삭제",
            description = "Soft Delete 방식으로 사용자 계정을 삭제합니다. 응답 본문은 없습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/me")
    ResponseEntity<Void> deleteMe(
            @RequestHeader("Authorization") String authorization
    );
}
