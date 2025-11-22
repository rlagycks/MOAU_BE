package com.moau.moau.notice.repository;

import com.moau.moau.notice.domain.NoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {
    List<NoticeImage> findAllByNoticeIdOrderBySortOrderAsc(Long noticeId);
}