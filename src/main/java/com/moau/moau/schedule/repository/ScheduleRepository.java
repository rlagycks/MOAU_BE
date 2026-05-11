package com.moau.moau.schedule.repository;

import com.moau.moau.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * Lazy Loading 방지: Team과 함께 조회
     */
    @Query("SELECT s FROM Schedule s JOIN FETCH s.team WHERE s.id = :scheduleId")
    Optional<Schedule> findByIdWithTeam(@Param("scheduleId") Long scheduleId);

    /**
     * [기간 일정 지원] 특정 팀의 일정 중, 조회 기간(startAt ~ endAt)과 겹치는(Overlap) 모든 일정을 조회합니다.
     * <p>
     * 조회 조건:
     * 1. 팀 ID 일치
     * 2. 일정 시작 시간이 조회 종료 시간보다 앞서야 함 (s.startsAt < :endAt)
     * 3. 일정 종료 시간이 조회 시작 시간보다 뒤에 있어야 함 (s.endsAt > :startAt)
     * <p>
     * 정렬 순서:
     * 1. 종일/기간 일정(isAllDay = true) 우선
     * 2. 시작 시간(startsAt) 오름차순
     */
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.team.id = :teamId " +
            "AND s.startsAt < :endAt " +
            "AND s.endsAt > :startAt " +
            "ORDER BY s.isAllDay DESC, s.startsAt ASC")
    List<Schedule> findOverlappingSchedules(
            @Param("teamId") Long teamId,
            @Param("startAt") Instant startAt,
            @Param("endAt") Instant endAt
    );

    /**
     * [기간 일정 지원] 여러 팀(내 캘린더)의 일정 중, 조회 기간과 겹치는 모든 일정을 조회합니다.
     * (로직은 위와 동일하며, 팀 ID 목록(IN 절)을 사용합니다.)
     */
    @Query("SELECT s FROM Schedule s " +
            "WHERE s.team.id IN :teamIds " +
            "AND s.startsAt < :endAt " +
            "AND s.endsAt > :startAt " +
            "ORDER BY s.isAllDay DESC, s.startsAt ASC")
    List<Schedule> findOverlappingSchedulesByTeamIds(
            @Param("teamIds") List<Long> teamIds,
            @Param("startAt") Instant startAt,
            @Param("endAt") Instant endAt
    );
}