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

    @Transactional
    public void vote(Long userId, Long teamId, Long pollId, VoteRequestDto dto) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new BusinessException(PollError.POLL_NOT_FOUND));

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        if (poll.isClosed()) {
            throw new BusinessException(PollError.CLOSED_POLL);
        }

        if (!poll.isAllowMultiple() && dto.pollOptionIds().size() > 1) {
            throw new BusinessException(PollError.MULTIPLE_SELECTION_NOT_ALLOWED);
        }

        // 기존 투표 취소 (Native Query로 Atomic 업데이트)
        List<PollVote> existingVotes = pollVoteRepository.findAllByPollIdAndUserId(pollId, userId);
        if (!existingVotes.isEmpty()) {
            for (PollVote vote : existingVotes) {
                pollOptionRepository.decrementVoteCount(vote.getPollOption().getId());
            }
            pollVoteRepository.deleteAllByPollIdAndUserId(pollId, userId);
        }

        // 새 투표 등록 (Native Query로 Atomic 업데이트)
        for (Long optionId : dto.pollOptionIds()) {
            PollOption option = pollOptionRepository.findById(optionId)
                    .orElseThrow(() -> new BusinessException(PollError.OPTION_NOT_FOUND));

            if (!option.getPoll().getId().equals(pollId)) {
                throw new BusinessException(PollError.INVALID_OPTION);
            }

            pollOptionRepository.incrementVoteCount(optionId);

            PollVote newVote = PollVote.builder()
                    .poll(poll)
                    .pollOption(option)
                    .userId(userId)
                    .build();
            pollVoteRepository.save(newVote);
        }
    }
}