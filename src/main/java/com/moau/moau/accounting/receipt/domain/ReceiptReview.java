package com.moau.moau.accounting.receipt.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "receipt_reviews")
public class ReceiptReview extends BaseId {

    @OneToOne(fetch = FetchType.LAZY) // (정책: 1 Receipt : 1 Review)
    @JoinColumn(name = "receipt_id", nullable = false, unique = true)
    private Receipt receipt;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewStatus status;

    // --- 사용자가 확정한 최종 정보 ---
    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(nullable = false)
    private String description;
    // ---

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "approver_id")
    private Long approverId;

    @Builder
    public ReceiptReview(Receipt receipt, Long teamId, Long requesterId, Long amountCents,
                         LocalDate transactionDate, String merchantName, String paymentMethod, String description) {
        this.receipt = receipt;
        this.teamId = teamId;
        this.requesterId = requesterId;
        this.status = ReviewStatus.PENDING_APPROVAL; // 생성 시 '승인 대기'
        this.amountCents = amountCents;
        this.transactionDate = transactionDate;
        this.merchantName = merchantName;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.requestedAt = Instant.now();
    }

    // (승인 메서드)
    public void approve(Long approverId) {
        if (this.status != ReviewStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Not pending approval status");
        }
        this.status = ReviewStatus.APPROVED;
        this.approverId = approverId;
        this.approvedAt = Instant.now();
    }

    // (반려 메서드)
    public void reject(Long approverId, String reason) {
        if (this.status != ReviewStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Not pending approval status");
        }
        this.status = ReviewStatus.REJECTED;
        this.approverId = approverId;
        this.rejectedAt = Instant.now();
        this.rejectReason = reason;
    }
}