package com.moau.moau.notice.service;

import com.moau.moau.accounting.receipt.port.StorageServicePort;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.notice.domain.Notice;
import com.moau.moau.notice.dto.response.NoticeDetailResponseDto;
import com.moau.moau.notice.dto.response.NoticeResponseDto;
import com.moau.moau.notice.dto.response.PollDto;
import com.moau.moau.global.exception.error.NoticeError;
import com.moau.moau.notice.repository.NoticeImageRepository;
import com.moau.moau.notice.repository.NoticeRepository;
import com.moau.moau.poll.domain.Poll;
import com.moau.moau.poll.domain.PollOption;
import com.moau.moau.poll.repository.PollOptionRepository;
import com.moau.moau.poll.repository.PollRepository;
import com.moau.moau.poll.repository.PollVoteRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollVoteRepository pollVoteRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final StorageServicePort storageServicePort;

    public Page<NoticeResponseDto> getNotices(Long teamId, Pageable pageable) {
        if (!teamRepository.existsById(teamId)) {
            throw new BusinessException(CommonError.TEAM_NOT_FOUND);
        }

        Page<Notice> notices = noticeRepository.findAllByTeamIdAndDeletedAtIsNull(teamId, pageable);

        // N+1 쿼리 방지: 작성자 ID 수집 후 일괄 조회
        Set<Long> authorIds = notices.stream()
                .map(Notice::getAuthorUserId)
                .collect(Collectors.toSet());

        Map<Long, String> userNicknameMap = userRepository.findAllByIdIn(authorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));

        return notices.map(notice -> {
                    String authorName = userNicknameMap.getOrDefault(notice.getAuthorUserId(), "(알수없음)");
                    String preview = notice.getContent().length() > 50 ?
                            notice.getContent().substring(0, 50) + "..." : notice.getContent();

                    return new NoticeResponseDto(
                            notice.getId(),
                            notice.getTitle(),
                            preview,
                            authorName,
                            notice.isPinned(),
                            notice.isHasPoll(),
                            notice.getCreatedAt()
                    );
                });
    }

    public NoticeDetailResponseDto getNoticeDetail(Long currentUserId, Long teamId, Long noticeId) {
        Notice notice = noticeRepository.findByIdAndTeamIdAndDeletedAtIsNull(noticeId, teamId)
                .orElseThrow(() -> new BusinessException(NoticeError.NOTICE_NOT_FOUND));

        String authorName = getAuthorName(notice.getAuthorUserId());
        boolean isMyNotice = notice.getAuthorUserId().equals(currentUserId);

        // 2. 이미지 조회 -> S3 Pre-signed URL 변환
        List<String> imageUrls = noticeImageRepository.findAllByNoticeIdOrderBySortOrderAsc(noticeId)
                .stream()
                .map(image -> storageServicePort.createPresignedGetUrl(image.getImageKey())) // Key -> URL
                .toList();

        // 3. 투표 정보 조회 (있을 경우)
        PollDto pollDto = null;
        if (notice.isHasPoll()) {
            pollDto = getPollDto(notice.getId(), currentUserId);
        }

        return new NoticeDetailResponseDto(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                authorName,
                notice.isPinned(),
                isMyNotice,
                notice.getCreatedAt(),
                imageUrls,
                pollDto
        );
    }

    private PollDto getPollDto(Long noticeId, Long currentUserId) {
        Poll poll = pollRepository.findByNoticeId(noticeId).orElse(null);
        if (poll == null) return null;

        // 1. 옵션 목록 조회
        List<PollOption> options = pollOptionRepository.findAllByPollId(poll.getId());

        // 2. 내가 투표한 옵션 ID 목록 조회
        Set<Long> myVotedOptionIds = pollVoteRepository.findAllByPollIdAndUserId(poll.getId(), currentUserId)
                .stream()
                .map(vote -> vote.getPollOption().getId())
                .collect(Collectors.toSet());

        boolean isVoted = !myVotedOptionIds.isEmpty();

        // 3. 총 투표 수 계산
        int totalVoteCount = options.stream().mapToInt(PollOption::getVoteCount).sum();

        // 4. DTO 변환
        List<PollDto.PollOptionDto> optionDtos = options.stream()
                .map(opt -> new PollDto.PollOptionDto(
                        opt.getId(),
                        opt.getText(),
                        opt.getVoteCount(),
                        myVotedOptionIds.contains(opt.getId()) // 내가 찍은 항목이면 true
                ))
                .toList();

        return new PollDto(
                poll.getId(),
                poll.getTitle(),
                poll.isAllowMultiple(),
                poll.isAnonymous(),
                poll.isClosed(),
                poll.getDeadline(),
                optionDtos,
                isVoted,
                totalVoteCount
        );
    }

    private String getAuthorName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getNickname)
                .orElse("(알수없음)");
    }
}