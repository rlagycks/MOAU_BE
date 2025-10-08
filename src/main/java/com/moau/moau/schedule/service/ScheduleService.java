package com.moau.moau.schedule.service; // 패키지 경로 수정됨

import com.moau.moau.schedule.repository.ScheduleRepository; // ScheduleRepository의 경로 수정됨
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    // TODO: 내 캘린더 조회 로직
    // TODO: 그룹 캘린더 조회 로직
    // TODO: 일정 생성 로직 (@Transactional 필요)
    // TODO: 단일 일정 수정/삭제 로직 (@Transactional 필요)
    // TODO: 반복 일정 전체 수정/삭제 로직 (@Transactional 필요)
}