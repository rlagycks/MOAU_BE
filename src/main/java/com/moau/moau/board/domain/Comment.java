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
@Table(name = "comments")
public class Comment extends BaseSoftDelete {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "author_user_id", nullable = false)
    private Long authorUserId;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    @Column(name = "parent_id")
    private Long parentId;

    @Builder
    public Comment(Post post, Long authorUserId, String content, boolean isAnonymous, Long parentId) {
        this.post = post;
        this.authorUserId = authorUserId;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.parentId = parentId;
    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        this.markDeleted(Instant.now());
    }
}