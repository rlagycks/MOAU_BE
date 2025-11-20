package com.moau.moau.team.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.global.domain.BaseSoftDelete;
import jakarta.persistence.*;
import lombok.*;
import com.moau.moau.user.domain.User;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TEAMS")
public class Team extends BaseSoftDelete {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "invite_code", length = 8, unique = true)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "dues_period", nullable = false)
    @Builder.Default
    private DuesPeriod duesPeriod = DuesPeriod.NONE;

    @Column(name = "dues_amount", nullable = false)
    @Builder.Default
    private Long duesAmount = 0L;

    public void updateDuesSetting(DuesPeriod period, Long amount) {
        this.duesPeriod = period;
        this.duesAmount = amount;
    }
}