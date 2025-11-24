package com.moau.moau.notice.repository;

import com.moau.moau.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 1. 목록 조회
    Page<Notice> findAllByTeamIdAndDeletedAtIsNull(Long teamId, Pageable pageable);

    // 2. 상세 조회
    Optional<Notice> findByIdAndDeletedAtIsNull(Long id);

    // 3. 수정/삭제 시 권한 및 소유권 확인
    Optional<Notice> findByIdAndTeamIdAndDeletedAtIsNull(Long id, Long teamId);
}