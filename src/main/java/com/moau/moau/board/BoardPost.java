package com.moau.moau.board;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "BOARD_POSTS")
public class BoardPost extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Lob // MEDIUMTEXT, LONGTEXT 등 대용량 텍스트를 위한 어노테이션
    @Column(nullable = false)
    private String body;

    // @Enumerated(EnumType.STRING)
    private String status; // ENUM 타입

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false; // 기본값을 false로 설정
}