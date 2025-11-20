package com.moau.moau.accounting.dues.repository;

import com.moau.moau.accounting.dues.domain.DuesCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DuesCycleRepository extends JpaRepository<DuesCycle, Long> {

    // 1. 팀 ID와 시작 날짜로 이미 생성된 주기가 있는지 조회
    Optional<DuesCycle> findByTeamIdAndStartDate(Long teamId, LocalDate startDate);

    // 2. 팀의 모든 주기를 최신순으로 조회
    List<DuesCycle> findAllByTeamIdOrderByStartDateDesc(Long teamId);

    // 3. ID와 팀 ID 일치 여부 확인
    Optional<DuesCycle> findByIdAndTeamId(Long id, Long teamId);
}