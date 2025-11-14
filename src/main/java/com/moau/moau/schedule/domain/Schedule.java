package com.moau.moau.schedule.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.team.domain.Team; // Group 엔티티 import
import jakarta.persistence.*;
import lombok.AccessLevel;
import com.moau.moau.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "SCHEDULES")
public class Schedule extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Team team;

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