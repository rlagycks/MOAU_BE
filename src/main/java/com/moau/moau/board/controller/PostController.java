package com.moau.moau.board.controller;

import com.moau.moau.board.dto.request.CommentRequestDto;
import com.moau.moau.board.dto.request.PostRequestDto;
import com.moau.moau.board.dto.response.PostDetailResponseDto;
import com.moau.moau.board.dto.response.PostResponseDto;
import com.moau.moau.board.service.CommentCommandService;
import com.moau.moau.board.service.PostCommandService;
import com.moau.moau.board.service.PostQueryService;
import com.moau.moau.global.payload.ResponseDto;
import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.global.security.CheckTeamRole;
import com.moau.moau.team.domain.TeamMemberRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController implements PostControllerSwagger {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;
    private final CommentCommandService commentCommandService;

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<Long>> createPost(
            @PathVariable Long teamId,
            @Valid @RequestBody PostRequestDto requestDto
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long postId = postCommandService.createPost(userId, teamId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.data(postId));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<Page<PostResponseDto>>> getPosts(
            @PathVariable Long teamId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ResponseDto.data(postQueryService.getPosts(userId, teamId, pageable)));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<PostDetailResponseDto>> getPostDetail(
            @PathVariable Long teamId,
            @PathVariable Long postId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ResponseDto.data(postQueryService.getPostDetail(userId, teamId, postId)));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<Void>> updatePost(
            @PathVariable Long teamId,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequestDto requestDto
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        postCommandService.updatePost(userId, postId, requestDto);
        return ResponseEntity.ok(ResponseDto.message("게시글이 수정되었습니다."));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<Void>> deletePost(
            @PathVariable Long teamId,
            @PathVariable Long postId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        postCommandService.deletePost(userId, postId);
        return ResponseEntity.ok(ResponseDto.message("게시글이 삭제되었습니다."));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<Long>> createComment(
            @PathVariable Long teamId,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDto requestDto
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long commentId = commentCommandService.createComment(userId, postId, requestDto);
        return ResponseEntity.ok(ResponseDto.data(commentId));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<Void>> deleteComment(
            @PathVariable Long teamId,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        commentCommandService.deleteComment(userId, commentId);
        return ResponseEntity.ok(ResponseDto.message("댓글이 삭제되었습니다."));
    }
}