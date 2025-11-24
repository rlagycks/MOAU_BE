package com.moau.moau.poll.repository;

import com.moau.moau.poll.domain.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PollVoteRepository extends JpaRepository<PollVote, Long> {

    // 1. 특정 유저가 특정 투표 목록
    List<PollVote> findAllByPollIdAndUserId(Long pollId, Long userId);

    // 2. 특정 유저가 특정 옵션에 투표했는지 확인
    boolean existsByPollOptionIdAndUserId(Long pollOptionId, Long userId);
    
    void deleteAllByPollIdAndUserId(Long pollId, Long userId);
}