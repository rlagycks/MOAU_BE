package com.moau.moau.notice.controller;

import com.moau.moau.accounting.receipt.dto.request.PresignedUrlRequestDto;
import com.moau.moau.accounting.receipt.dto.response.PresignedUrlResponseDto;
import com.moau.moau.global.payload.ResponseDto;
import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.global.security.CheckTeamRole;
import com.moau.moau.notice.dto.request.NoticeCreateRequestDto;
import com.moau.moau.notice.dto.request.VoteRequestDto;
import com.moau.moau.notice.dto.response.NoticeDetailResponseDto;
import com.moau.moau.notice.dto.response.NoticeResponseDto;
import com.moau.moau.notice.service.NoticeCommandService;
import com.moau.moau.notice.service.NoticeQueryService;
import com.moau.moau.poll.service.PollCommandService;
import com.moau.moau.team.domain.TeamMemberRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController implements NoticeControllerSwagger {

    private final NoticeCommandService noticeCommandService;
    private final NoticeQueryService noticeQueryService;
    private final PollCommandService pollCommandService;

    @Override
    @CheckTeamRole(TeamMemberRole.ADMIN)
    public ResponseEntity<ResponseDto<PresignedUrlResponseDto>> createPresignedUrl(
            @PathVariable Long teamId,
            @Valid @RequestBody PresignedUrlRequestDto requestDto
    ) {
        PresignedUrlResponseDto responseDto = noticeCommandService.createPresignedUrl(requestDto.filename());
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.ADMIN)
    public ResponseEntity<ResponseDto<Long>> createNotice(
            @PathVariable Long teamId,
            @Valid @RequestBody NoticeCreateRequestDto requestDto
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        Long noticeId = noticeCommandService.createNotice(userId, teamId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.data(noticeId));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<Page<NoticeResponseDto>>> getNotices(
            @PathVariable Long teamId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ResponseDto.data(noticeQueryService.getNotices(teamId, pageable)));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<NoticeDetailResponseDto>> getNoticeDetail(
            @PathVariable Long teamId,
            @PathVariable Long noticeId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ResponseDto.data(noticeQueryService.getNoticeDetail(userId, teamId, noticeId)));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.ADMIN)
    public ResponseEntity<ResponseDto<Void>> deleteNotice(
            @PathVariable Long teamId,
            @PathVariable Long noticeId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        noticeCommandService.deleteNotice(userId, teamId, noticeId);
        return ResponseEntity.ok(ResponseDto.message("공지사항이 삭제되었습니다."));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.MEMBER)
    public ResponseEntity<ResponseDto<Void>> vote(
            @PathVariable Long teamId,
            @PathVariable Long pollId,
            @Valid @RequestBody VoteRequestDto requestDto
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        pollCommandService.vote(userId, teamId, pollId, requestDto);
        return ResponseEntity.ok(ResponseDto.message("투표가 완료되었습니다."));
    }
}