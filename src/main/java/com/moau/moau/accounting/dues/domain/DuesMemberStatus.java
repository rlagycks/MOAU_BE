package com.moau.moau.accounting.dues.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "dues_member_statuses")
public class DuesMemberStatus extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", nullable = false)
    private DuesCycle cycle;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DuesStatus status;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Builder
    public DuesMemberStatus(DuesCycle cycle, Long userId, Long amount) {
        this.cycle = cycle;
        this.userId = userId;
        this.status = DuesStatus.UNPAID;
        this.amount = amount;
    }

    public void markPaid() {
        this.status = DuesStatus.PAID;
        this.paidAt = Instant.now();
    }

    public void markUnpaid() {
        this.status = DuesStatus.UNPAID;
        this.paidAt = null;
    }
}