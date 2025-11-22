package com.moau.moau.notice.service;

import com.moau.moau.accounting.receipt.port.StorageServicePort;
import com.moau.moau.accounting.receipt.dto.response.PresignedUrlResponseDto;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.notice.domain.Notice;
import com.moau.moau.notice.domain.NoticeImage;
import com.moau.moau.notice.dto.request.NoticeCreateRequestDto;
import com.moau.moau.global.exception.error.NoticeError;
import com.moau.moau.notice.repository.NoticeImageRepository;
import com.moau.moau.notice.repository.NoticeRepository;
import com.moau.moau.poll.domain.Poll;
import com.moau.moau.poll.domain.PollOption;
import com.moau.moau.poll.repository.PollOptionRepository;
import com.moau.moau.poll.repository.PollRepository;
import com.moau.moau.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeCommandService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final TeamRepository teamRepository;

    private final StorageServicePort storageServicePort;

    public PresignedUrlResponseDto createPresignedUrl(String filename) {
        return storageServicePort.createPresignedUrl(filename);
    }

    public Long createNotice(Long userId, Long teamId, NoticeCreateRequestDto dto) {
        if (!teamRepository.existsById(teamId)) {
            throw new BusinessException(CommonError.TEAM_NOT_FOUND);
        }

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

        if (dto.imageKeys() != null && !dto.imageKeys().isEmpty()) {
            List<NoticeImage> images = new ArrayList<>();
            for (int i = 0; i < dto.imageKeys().size(); i++) {
                images.add(NoticeImage.builder()
                        .notice(savedNotice)
                        .imageKey(dto.imageKeys().get(i))
                        .sortOrder(i)
                        .build());
            }
            noticeImageRepository.saveAll(images);
        }

        if (hasPoll) {
            createPoll(savedNotice, dto.poll());
        }

        return savedNotice.getId();
    }

    public void deleteNotice(Long userId, Long teamId, Long noticeId) {
        Notice notice = noticeRepository.findByIdAndTeamIdAndDeletedAtIsNull(noticeId, teamId)
                .orElseThrow(() -> new BusinessException(NoticeError.NOTICE_NOT_FOUND));

        if (!notice.getAuthorUserId().equals(userId)) {
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        notice.delete();
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

        List<PollOption> options = pollDto.options().stream()
                .map(text -> PollOption.builder()
                        .poll(savedPoll)
                        .text(text)
                        .build())
                .toList();
        pollOptionRepository.saveAll(options);
    }
}