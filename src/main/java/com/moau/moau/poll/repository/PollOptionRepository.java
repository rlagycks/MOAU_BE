package com.moau.moau.poll.repository;

import com.moau.moau.poll.domain.PollOption;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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
}