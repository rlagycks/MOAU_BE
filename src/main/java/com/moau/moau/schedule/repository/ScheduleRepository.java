package com.moau.moau.schedule.repository;

import com.moau.moau.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // [✅ 추가할 코드]
    /**
     * 특정 그룹의 특정 기간(시작일 ~ 종료일) 사이의 모든 일정을 조회합니다.
     * @param groupId 그룹 ID
     * @param startAt 조회 시작 시간
     * @param endAt 조회 종료 시간
     * @return 일정 목록
     */
    List<Schedule> findByGroup_IdAndStartsAtBetween(Long groupId, Instant startAt, Instant endAt);
}