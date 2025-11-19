package com.moau.moau.schedule.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.team.domain.Team;
import com.moau.moau.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SCHEDULES")
public class Schedule extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID", nullable = false)
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

    @Column(name = "is_all_day", nullable = false)
    private boolean isAllDay;

    @Column(name = "recurring_id", length = 36)
    private String recurringId;

    public void update(
            String title,
            String description,
            String location,
            Instant startsAt,
            Instant endsAt,
            boolean isAllDay
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.isAllDay = isAllDay;
    }
}