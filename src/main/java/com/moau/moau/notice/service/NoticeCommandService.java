package com.moau.moau.notice.service;

import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.notice.domain.Notice;
import com.moau.moau.notice.dto.request.NoticeCreateRequestDto;
import com.moau.moau.global.exception.error.NoticeError;
import com.moau.moau.notice.repository.NoticeRepository;
import com.moau.moau.poll.domain.Poll;
import com.moau.moau.poll.domain.PollOption;
import com.moau.moau.poll.repository.PollOptionRepository;
import com.moau.moau.poll.repository.PollRepository;
import com.moau.moau.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeCommandService {

    private final NoticeRepository noticeRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final TeamRepository teamRepository;

    // 공지사항 생성 (투표 포함 가능)
    public Long createNotice(Long userId, Long teamId, NoticeCreateRequestDto dto) {
        if (!teamRepository.existsById(teamId)) {
            throw new BusinessException(CommonError.TEAM_NOT_FOUND);
        }

        // 1. Notice 생성
        boolean hasPoll = (dto.poll() != null);
        Notice notice = Notice.builder()
                .teamId(teamId)
                .authorUserId(userId)
                .title(dto.title())
                .content(dto.content())
                .isPinned(dto.isPinned())
                .hasPoll(hasPoll)
                .build();

        Notice savedNotice = noticeRepository.save(notice);

        // 2. Poll 생성 (있다면)
        if (hasPoll) {
            createPoll(savedNotice, dto.poll());
        }

        return savedNotice.getId();
    }

    private void createPoll(Notice notice, NoticeCreateRequestDto.PollCreateDto pollDto) {
        Poll poll = Poll.builder()
                .notice(notice)
                .title(pollDto.title())
                .allowMultiple(pollDto.allowMultiple())
                .isAnonymous(pollDto.isAnonymous())
                .deadline(pollDto.deadline())
                .build();
        Poll savedPoll = pollRepository.save(poll);

        // 옵션 생성
        List<PollOption> options = pollDto.options().stream()
                .map(text -> PollOption.builder()
                        .poll(savedPoll)
                        .text(text)
                        .build())
                .toList();
        pollOptionRepository.saveAll(options);
    }

    // 공지 삭제
    public void deleteNotice(Long userId, Long noticeId) {
        Notice notice = noticeRepository.findByIdAndDeletedAtIsNull(noticeId)
                .orElseThrow(() -> new BusinessException(NoticeError.NOTICE_NOT_FOUND));
        notice.delete();
    }
}