package com.moau.moau.poll.repository;

import com.moau.moau.poll.domain.PollOption;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PollOptionRepository extends JpaRepository<PollOption, Long> {

    List<PollOption> findAllByPollId(Long pollId);

    // 2. [핵심] 투표 카운트 증가/감소 시 사용 (비관적 락)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT po FROM PollOption po WHERE po.id = :id")
    Optional<PollOption> findByIdWithLock(@Param("id") Long id);

    // 3. Race Condition 방지: Atomic 업데이트 (Native Query)
    @Modifying
    @Query("UPDATE PollOption po SET po.voteCount = po.voteCount + 1 WHERE po.id = :optionId")
    void incrementVoteCount(@Param("optionId") Long optionId);

    @Modifying
    @Query("UPDATE PollOption po SET po.voteCount = po.voteCount - 1 WHERE po.id = :optionId AND po.voteCount > 0")
    void decrementVoteCount(@Param("optionId") Long optionId);
}