package com.moau.moau.team.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import com.moau.moau.user.domain.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "JOIN_REQUESTS")
public class JoinRequest extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_user_id", nullable = false)
    private User requestUser;

    @Column(nullable = false)
    // @Enumerated(EnumType.STRING)
    private String status; // ENUM 타입 (예: PENDING, APPROVED, REJECTED)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by")
    private User decidedBy; // 요청을 승인/거절한 관리자

    // BaseId가 createdAt, updatedAt을 이미 가지고 있으므로,
    // ERD의 requested_at은 createdAt으로 대체하고 decided_at만 추가합니다.
    @Column(name = "decided_at")
    private Instant decidedAt;

}