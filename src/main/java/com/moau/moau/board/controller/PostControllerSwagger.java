package com.moau.moau.board.controller;

import com.moau.moau.board.dto.request.CommentRequestDto;
import com.moau.moau.board.dto.request.PostRequestDto;
import com.moau.moau.board.dto.response.PostDetailResponseDto;
import com.moau.moau.board.dto.response.PostResponseDto;
import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.global.payload.ResponseDto;
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

@Tag(name = "💬 Community - Board", description = "게시판(게시글, 댓글) API")
@RequestMapping("/api/teams/{teamId}/posts")
public interface PostControllerSwagger {

    @Operation(summary = "게시글 작성", description = "(Auth: MEMBER) 새로운 게시글을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "작성 성공")
    @PostMapping
    ResponseEntity<ResponseDto<Long>> createPost(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Valid @RequestBody PostRequestDto requestDto
    );

    @Operation(summary = "게시글 목록 조회", description = "(Auth: MEMBER) 게시글 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    ResponseEntity<ResponseDto<Page<PostResponseDto>>> getPosts(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "게시글 상세 조회", description = "(Auth: MEMBER) 게시글 내용과 댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{postId}")
    ResponseEntity<ResponseDto<PostDetailResponseDto>> getPostDetail(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId
    );

    @Operation(summary = "게시글 수정", description = "(Auth: MEMBER) 본인이 작성한 게시글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/{postId}")
    ResponseEntity<ResponseDto<Void>> updatePost(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
            @Valid @RequestBody PostRequestDto requestDto
    );

    @Operation(summary = "게시글 삭제", description = "(Auth: MEMBER) 본인이 작성한 게시글을 삭제(Soft Delete)합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{postId}")
    ResponseEntity<ResponseDto<Void>> deletePost(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId
    );

    @Operation(summary = "댓글 작성", description = "(Auth: MEMBER) 게시글에 댓글을 작성합니다.")
    @ApiResponse(responseCode = "200", description = "작성 성공")
    @PostMapping("/{postId}/comments")
    ResponseEntity<ResponseDto<Long>> createComment(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDto requestDto
    );

    @Operation(summary = "댓글 삭제", description = "(Auth: MEMBER) 본인이 작성한 댓글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{postId}/comments/{commentId}")
    ResponseEntity<ResponseDto<Void>> deleteComment(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId
    );
}