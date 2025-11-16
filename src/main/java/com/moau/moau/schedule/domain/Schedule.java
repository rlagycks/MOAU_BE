package com.moau.moau.schedule.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.team.domain.Team;
import com.moau.moau.user.domain.User;
import com.moau.moau.team.domain.Team; // Group 엔티티 import
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.moau.moau.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SCHEDULES") // [✅ 규칙 통일]
public class Schedule extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID", nullable = false) // [✅ 수정] "group_id" -> "TEAM_ID"
    private Team team; // [✅ 수정] 참조 엔티티

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User creator;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    private String location;

    @Column(name = "recurring_id", length = 36)
    private String recurringId;
}