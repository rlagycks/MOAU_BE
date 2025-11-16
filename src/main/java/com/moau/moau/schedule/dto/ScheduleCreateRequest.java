package com.moau.moau.schedule.dto;

import com.moau.moau.schedule.domain.Schedule;
import com.moau.moau.team.domain.Team;
import com.moau.moau.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCreateRequest {

    private String title;
    private String description;
    private Instant startsAt;
    private Instant endsAt;
    private String location;
    // private RecurrenceDto recurrence; // 반복 일정은 다음 단계에서 구현

    @Builder
    public ScheduleCreateRequest(String title, String description, Instant startsAt, Instant endsAt, String location) {
        this.title = title;
        this.description = description;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.location = location;
    }

    // DTO를 Schedule 엔티티로 변환하는 메소드
    public Schedule toEntity(Team team, User creator) {
        return Schedule.builder()
                .team(team)
                .creator(creator)
                .title(this.title)
                .description(this.description)
                .startsAt(this.startsAt)
                .endsAt(this.endsAt)
                .location(this.location)
                .build();
    }
}