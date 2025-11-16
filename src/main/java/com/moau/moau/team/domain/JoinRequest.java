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
@Table(name = "JOIN_REQUESTS") // [✅ 수정] 대문자 복수형
public class JoinRequest extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID", nullable = false) // [✅ 수정] "group_id" -> "TEAM_ID"
    private Team team; // [✅ 수정] Group -> Team

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_USER_ID", nullable = false) // [✅ 수정] 대문자
    private User requestUser;

    @Column(nullable = false)
    private String status; // ENUM 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DECIDED_BY") // [✅ 수정] 대문자
    private User decidedBy;

    // BaseId가 createdAt을 "created_at"으로 가지고 있으므로,
    // ERD의 requested_at을 createdAt으로 사용하고 decided_at만 추가합니다.
    @Column(name = "decided_at")
    private Instant decidedAt;

}