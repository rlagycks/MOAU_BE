package com.moau.moau.notice.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.team.domain.Team;
import jakarta.persistence.*;
import com.moau.moau.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor; // [✅ @Builder를 위해 추가]
import lombok.Builder; // [✅ 추가]
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // [✅ @Builder를 위해 추가]
@Builder // [✅ 추가]
@Entity
@Table(name = "NOTICES") // [✅ 규칙 통일]
public class Notice extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID", nullable = false) // [✅ 수정] "group_id" -> "TEAM_ID"
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHOR_USER_ID", nullable = false) // [✅ 대문자 통일]
    private User author;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Column(nullable = false)
    private String status; // ENUM

    @Column(name = "published_at")
    private Instant publishedAt;
}