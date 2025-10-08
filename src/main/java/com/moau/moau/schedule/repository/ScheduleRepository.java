package com.moau.moau.schedule.repository; // 패키지 경로 수정됨

import com.moau.moau.schedule.domain.Schedule; // Schedule 엔티티의 경로 수정됨
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // JpaRepository<[엔티티 클래스], [엔티티의 ID 타입]>

    // 앞으로 필요한 커스텀 쿼리 메소드를 여기에 추가할 예정
}