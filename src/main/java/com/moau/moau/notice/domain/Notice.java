package com.moau.moau.notice.domain;

import com.moau.moau.global.domain.BaseSoftDelete;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notices")
public class Notice extends BaseSoftDelete {

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "author_user_id", nullable = false)
    private Long authorUserId;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned;

    @Column(name = "has_poll", nullable = false)
    private boolean hasPoll;

    @Builder
    public Notice(Long teamId, Long authorUserId, String title, String content, boolean isPinned, boolean hasPoll) {
        this.teamId = teamId;
        this.authorUserId = authorUserId;
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.hasPoll = hasPoll;
    }

    public void update(String title, String content, boolean isPinned) {
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
    }

    public void delete() {
        this.markDeleted(Instant.now());
    }
}