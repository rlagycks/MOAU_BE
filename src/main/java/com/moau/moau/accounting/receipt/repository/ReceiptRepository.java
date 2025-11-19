package com.moau.moau.accounting.receipt.repository;

import com.moau.moau.accounting.receipt.domain.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    // 영수증 상세 조회 (권한 체크용)
    Optional<Receipt> findByIdAndTeamId(Long id, Long teamId);

    // 특정 팀의 영수증 목록 페이징 조회
    // Page<Receipt> findByTeamId(Long teamId, Pageable pageable);
}