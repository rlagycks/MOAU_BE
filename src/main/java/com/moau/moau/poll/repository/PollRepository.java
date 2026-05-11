package com.moau.moau.poll.repository;

import com.moau.moau.poll.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findByNoticeId(Long noticeId);

    /**
     * N+1 쿼리 방지: PollOption들과 함께 조회
     */
    @Query("SELECT p FROM Poll p LEFT JOIN FETCH p.options WHERE p.notice.id = :noticeId")
    Optional<Poll> findByNoticeIdWithOptions(@Param("noticeId") Long noticeId);
}