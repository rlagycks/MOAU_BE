package com.moau.moau.accounting.receipt.domain;

public enum ReviewStatus {
    PENDING_REVIEW,     // (신규) OCR 완료 후, 사용자의 '승인 요청' 전 상태
    PENDING_APPROVAL,   // (기존) 승인 대기 중 (관리자 승인 대기)
    APPROVED,           // 승인 완료
    REJECTED            // 승인 거절
}