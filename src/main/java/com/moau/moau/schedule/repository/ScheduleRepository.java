package com.moau.moau.schedule.repository;

import com.moau.moau.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * [기존] 특정 팀(1개)의 특정 기간 사이의 모든 일정을 조회합니다.
     */
    List<Schedule> findByTeam_IdAndStartsAtBetween(Long teamId, Instant startAt, Instant endAt);

    /**
     * 특정 팀 목록(여러 개)에 포함되고, 특정 기간 사이의 모든 일정을 조회합니다.
     * @param teamIds 팀 ID 목록
     * @param startAt 조회 시작 시간
     * @param endAt 조회 종료 시간
     * @return 일정 목록
     */
    List<Schedule> findByTeam_IdInAndStartsAtBetween(List<Long> teamIds, Instant startAt, Instant endAt);
}