package com.moau.moau.poll.repository;

import com.moau.moau.poll.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findByNoticeId(Long noticeId);
}