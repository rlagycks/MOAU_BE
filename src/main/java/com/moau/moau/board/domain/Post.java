package com.moau.moau.board.domain;

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
@Table(name = "posts")
public class Post extends BaseSoftDelete {

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "author_user_id", nullable = false)
    private Long authorUserId;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    @Column(name = "comment_count", nullable = false)
    private int commentCount = 0;

    @Builder
    public Post(Long teamId, Long authorUserId, String title, String content, boolean isAnonymous) {
        this.teamId = teamId;
        this.authorUserId = authorUserId;
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
    }

    public void update(String title, String content, boolean isAnonymous) {
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) this.commentCount--;
    }

    public void delete() {
        this.markDeleted(Instant.now());
    }
}