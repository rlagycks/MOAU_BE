package com.moau.moau.notice.service;

import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.notice.domain.Notice;
import com.moau.moau.notice.dto.response.NoticeDetailResponseDto;
import com.moau.moau.notice.dto.response.NoticeResponseDto;
import com.moau.moau.notice.dto.response.PollDto;
import com.moau.moau.global.exception.error.NoticeError;
import com.moau.moau.notice.repository.NoticeRepository;
import com.moau.moau.poll.domain.Poll;
import com.moau.moau.poll.domain.PollOption;
import com.moau.moau.poll.domain.PollVote;
import com.moau.moau.poll.repository.PollOptionRepository;
import com.moau.moau.poll.repository.PollRepository;
import com.moau.moau.poll.repository.PollVoteRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeQueryService {

    private final NoticeRepository noticeRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollVoteRepository pollVoteRepository;
    private final UserRepository userRepository;

    // 목록 조회
    public Page<NoticeResponseDto> getNotices(Long teamId, Pageable pageable) {
        return noticeRepository.findAllByTeamIdAndDeletedAtIsNull(teamId, pageable)
                .map(notice -> {
                    String authorName = getAuthorName(notice.getAuthorUserId());
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

    // 상세 조회 (투표 포함)
    public NoticeDetailResponseDto getNoticeDetail(Long currentUserId, Long teamId, Long noticeId) {
        Notice notice = noticeRepository.findByIdAndTeamIdAndDeletedAtIsNull(noticeId, teamId)
                .orElseThrow(() -> new BusinessException(NoticeError.NOTICE_NOT_FOUND));

        String authorName = getAuthorName(notice.getAuthorUserId());
        boolean isMyNotice = notice.getAuthorUserId().equals(currentUserId);

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
                pollDto
        );
    }

    // --- Helper: 투표 정보 조립 ---
    private PollDto getPollDto(Long noticeId, Long currentUserId) {
        Poll poll = pollRepository.findByNoticeId(noticeId).orElse(null);
        if (poll == null) return null;

        // 1. 옵션 목록 조회
        List<PollOption> options = pollOptionRepository.findAllByPollId(poll.getId());

        // 2. 내가 투표한 내역 조회 (Set으로 변환하여 조회 성능 O(1))
        Set<Long> myVotedOptionIds = pollVoteRepository.findAllByPollIdAndUserId(poll.getId(), currentUserId)
                .stream()
                .map(vote -> vote.getPollOption().getId())
                .collect(Collectors.toSet());

        boolean isVoted = !myVotedOptionIds.isEmpty();

        // 3. 총 투표자 수 계산 (단순 합계가 아니라 Unique User Count가 필요하면 쿼리 필요)
        // 여기서는 단순 옵션별 득표수 합계를 보여주거나, PollVote count query를 날려야 함.
        // MVP: 옵션별 voteCount 합계로 근사치 사용 (복수선택 시 중복 집계됨)
        // 정확히 하려면: pollVoteRepository.countDistinctUserIdByPollId(poll.getId())
        int totalVoteCount = options.stream().mapToInt(PollOption::getVoteCount).sum();

        // 4. DTO 변환
        List<PollDto.PollOptionDto> optionDtos = options.stream()
                .map(opt -> new PollDto.PollOptionDto(
                        opt.getId(),
                        opt.getText(),
                        opt.getVoteCount(),
                        myVotedOptionIds.contains(opt.getId()) // 내가 찍었는지
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
        return userRepository.findById(userId).map(User::getNickname).orElse("(알수없음)");
    }
}