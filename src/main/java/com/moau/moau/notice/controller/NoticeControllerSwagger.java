package com.moau.moau.notice.controller;

import com.moau.moau.accounting.receipt.dto.request.PresignedUrlRequestDto;
import com.moau.moau.accounting.receipt.dto.response.PresignedUrlResponseDto;
import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.global.payload.ResponseDto;
import com.moau.moau.notice.dto.request.NoticeCreateRequestDto;
import com.moau.moau.notice.dto.request.VoteRequestDto;
import com.moau.moau.notice.dto.response.NoticeDetailResponseDto;
import com.moau.moau.notice.dto.response.NoticeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "📢 Notice & Poll", description = "공지사항 및 투표 관리 API")
@RequestMapping("/api/teams/{teamId}/notices")
public interface NoticeControllerSwagger {

    // [신규 추가] 이미지 업로드 URL 발급
    @Operation(summary = "공지 이미지 업로드 URL 발급", description = "(Auth: ADMIN) S3에 이미지를 업로드하기 위한 Pre-signed URL을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "발급 성공")
    @PostMapping("/upload-url")
    ResponseEntity<ResponseDto<PresignedUrlResponseDto>> createPresignedUrl(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Valid @RequestBody PresignedUrlRequestDto requestDto
    );

    @Operation(summary = "공지사항 작성", description = "(Auth: ADMIN) 공지사항을 작성합니다. (투표 및 이미지 키 리스트 포함 가능)")
    @ApiResponse(responseCode = "201", description = "작성 성공")
    @PostMapping
    ResponseEntity<ResponseDto<Long>> createNotice(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Valid @RequestBody NoticeCreateRequestDto requestDto
    );

    // ... (나머지 API 동일)
    @Operation(summary = "공지사항 목록 조회", description = "(Auth: MEMBER) 공지사항 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    ResponseEntity<ResponseDto<Page<NoticeResponseDto>>> getNotices(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "공지사항 상세 조회", description = "(Auth: MEMBER) 공지사항 내용과 투표(있는 경우) 현황을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "공지 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{noticeId}")
    ResponseEntity<ResponseDto<NoticeDetailResponseDto>> getNoticeDetail(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "공지 ID", required = true) @PathVariable Long noticeId
    );

    @Operation(summary = "공지사항 삭제", description = "(Auth: ADMIN) 공지사항을 삭제(Soft Delete)합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{noticeId}")
    ResponseEntity<ResponseDto<Void>> deleteNotice(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "공지 ID", required = true) @PathVariable Long noticeId
    );

    @Operation(summary = "투표하기", description = "(Auth: MEMBER) 투표를 행사합니다. (재투표 시 기존 내역 삭제 후 반영)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "투표 성공"),
            @ApiResponse(responseCode = "400", description = "마감됨 / 복수선택 불가 / 잘못된 옵션", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "투표 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/polls/{pollId}/vote")
    ResponseEntity<ResponseDto<Void>> vote(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "투표 ID", required = true) @PathVariable Long pollId,
            @Valid @RequestBody VoteRequestDto requestDto
    );
}