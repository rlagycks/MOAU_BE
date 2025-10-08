package com.moau.moau.board.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.group.domain.Group;
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
    private Group group;

    @Column(nullable = false)
    private String name;
}