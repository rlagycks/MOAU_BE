package com.moau.moau.accounting.receipt.repository;

import com.moau.moau.accounting.receipt.domain.ReceiptReview;
import com.moau.moau.accounting.receipt.domain.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReceiptReviewRepository extends JpaRepository<ReceiptReview, Long> {

    // (승인/반려 시) 권한 체크용
    Optional<ReceiptReview> findByIdAndTeamId(Long id, Long teamId);

    // (승인 요청 시) 특정 영수증(Receipt)에 대해 이미 처리되지 않은 요청이 있는지 확인
    boolean existsByReceiptIdAndStatusIn(Long receiptId, java.util.Collection<ReviewStatus> statuses);

    // 관리자 승인 대기 목록 조회 (페이징)
    Page<ReceiptReview> findByTeamIdAndStatus(Long teamId, ReviewStatus status, Pageable pageable);

    // (ReceiptDetailDto 조회용)
    Optional<ReceiptReview> findByReceiptId(Long receiptId);
}