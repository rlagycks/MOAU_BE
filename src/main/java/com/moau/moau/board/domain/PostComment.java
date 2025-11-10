package com.moau.moau.board.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor; // [✅ @Builder를 위해 추가]
import lombok.Builder; // [✅ 추가]
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // [✅ @Builder를 위해 추가]
@Builder // [✅ 추가]
@Entity
@Table(name = "POST_COMMENTS") // [✅ 규칙 통일]
public class PostComment extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false) // [✅ 대문자 통일]
    private BoardPost boardPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID") // [✅ 대문자 통일]
    private PostComment parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHOR_USER_ID", nullable = false) // [✅ 대문자 통일]
    private User author;

    @Column(nullable = false)
    private Integer depth; // 0: 최상위 댓글, 1: 대댓글

    @Column(length = 1000)
    private String path; // 댓글 계층 구조 경로

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(nullable = false) // [✅ NotNull 추가, ERD 기반]
    private String status;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}