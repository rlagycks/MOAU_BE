package com.moau.moau.notice.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.team.domain.Team;
import com.moau.moau.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "NOTICES")
public class Notice extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    // @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String status;

    @Column(name = "published_at")
    private Instant publishedAt; // 게시 시각
}