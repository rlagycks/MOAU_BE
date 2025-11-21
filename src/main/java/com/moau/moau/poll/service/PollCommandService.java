package com.moau.moau.poll.service;

import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.notice.dto.request.VoteRequestDto;
import com.moau.moau.poll.domain.Poll;
import com.moau.moau.poll.domain.PollOption;
import com.moau.moau.poll.domain.PollVote;
import com.moau.moau.global.exception.error.PollError;
import com.moau.moau.poll.repository.PollOptionRepository;
import com.moau.moau.poll.repository.PollRepository;
import com.moau.moau.poll.repository.PollVoteRepository;
import com.moau.moau.team.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PollCommandService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollVoteRepository pollVoteRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 투표하기 (생성 및 수정 통합)
     * - 기존 투표 내역이 있으면 취소(차감) 후 재투표(증가)
     * - 비관적 락 사용
     */
    @Transactional
    public void vote(Long userId, Long teamId, Long pollId, VoteRequestDto dto) {
        // 1. 기본 검증
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new BusinessException(PollError.POLL_NOT_FOUND));

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        // 마감 체크
        if (poll.isClosed()) {
            throw new BusinessException(PollError.CLOSED_POLL);
        }

        // 복수 선택 검증
        if (!poll.isAllowMultiple() && dto.pollOptionIds().size() > 1) {
            throw new BusinessException(PollError.MULTIPLE_SELECTION_NOT_ALLOWED);
        }

        // 2. [취소 로직] 기존 투표 내역이 있다면 삭제하고 카운트 감소
        List<PollVote> existingVotes = pollVoteRepository.findAllByPollIdAndUserId(pollId, userId);
        if (!existingVotes.isEmpty()) {
            for (PollVote vote : existingVotes) {
                // [LOCK] 감소시킬 옵션에 락 걸고 조회
                PollOption option = pollOptionRepository.findByIdWithLock(vote.getPollOption().getId())
                        .orElseThrow(() -> new BusinessException(PollError.OPTION_NOT_FOUND));

                option.decreaseVote(); // 카운트 -1
                // (Dirty Checking으로 자동 저장됨)
            }
            pollVoteRepository.deleteAll(existingVotes); // 내역 삭제
        }

        // 3. [투표 로직] 새 항목에 대해 투표 생성하고 카운트 증가
        for (Long optionId : dto.pollOptionIds()) {
            // [LOCK] 증가시킬 옵션에 락 걸고 조회 (동시성 제어 핵심)
            PollOption option = pollOptionRepository.findByIdWithLock(optionId)
                    .orElseThrow(() -> new BusinessException(PollError.OPTION_NOT_FOUND));

            // 옵션이 해당 투표 소속인지 검증 (안전장치)
            if (!option.getPoll().getId().equals(pollId)) {
                throw new BusinessException(PollError.INVALID_OPTION);
            }

            option.increaseVote(); // 카운트 +1

            // 내역 저장
            PollVote newVote = PollVote.builder()
                    .poll(poll)
                    .pollOption(option)
                    .userId(userId)
                    .build();
            pollVoteRepository.save(newVote);
        }
    }
}