package com.moau.moau.board.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "BOARDS")
public class Board extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private String name;
}