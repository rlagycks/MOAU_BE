package com.moau.moau.accounting.dues.repository;

import com.moau.moau.accounting.dues.domain.DuesCycle;
import com.moau.moau.accounting.dues.domain.DuesMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DuesMemberStatusRepository extends JpaRepository<DuesMemberStatus, Long> {

    // 1. 특정 주기의 모든 멤버 상태 조회
    List<DuesMemberStatus> findAllByCycle(DuesCycle cycle);

    // 2. 특정 주기 + 특정 유저의 상태 조회
    Optional<DuesMemberStatus> findByCycleAndUserId(DuesCycle cycle, Long userId);

    // 3. 특정 주기의 멤버 상태 일괄 삭제
    @Modifying
    @Query("DELETE FROM DuesMemberStatus d WHERE d.cycle = :cycle")
    void deleteAllByCycle(@Param("cycle") DuesCycle cycle);
}