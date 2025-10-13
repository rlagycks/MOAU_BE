package com.moau.moau.board.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import com.moau.moau.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "POST_COMMENTS")
public class PostComment extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private BoardPost boardPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id") // 자기 자신을 참조. 최상위 댓글은 null.
    private PostComment parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private Integer depth; // 0: 최상위 댓글, 1: 대댓글, 2: 대대댓글

    @Column(length = 1000)
    private String path; // 댓글의 계층 구조를 표현. 예: "1/5/12/"

    @Lob
    @Column(nullable = false)
    private String body;

    // @Enumerated(EnumType.STRING)
    private String status;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}