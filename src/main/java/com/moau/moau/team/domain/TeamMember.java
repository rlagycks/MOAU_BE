package com.moau.moau.team.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import com.moau.moau.user.domain.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter // 이미 있으면 또 쓸 필요 없음
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TEAM_MEMBERS") // [✅ 수정] 대문자 복수형
public class TeamMember {

    @EmbeddedId
    private TeamMemberId id;

    @MapsId("teamId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID") // [ 수정] 대문자
    private Team team;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID") // [ 수정] 대문자
    private User user;

    @Column(nullable = false)
    private String role; // ENUM

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TeamMemberStatus status; // ACTIVE / PENDING / LEFT

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;
}