package com.moau.moau.accounting.dues.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "dues_cycles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"team_id", "start_date", "end_date"})
})
public class DuesCycle extends BaseId {

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder
    public DuesCycle(Long teamId, String name, LocalDate startDate, LocalDate endDate) {
        this.teamId = teamId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}