package com.moau.moau.board.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // [✅ @Builder를 위해 추가]
@Builder // [✅ 추가]
@Entity
@Table(name = "BOARD_POSTS") // [✅ 규칙 통일]
public class BoardPost extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID", nullable = false) // [✅ 대문자 통일]
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHOR_USER_ID", nullable = false) // [✅ 대문자 통일]
    private User author;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(nullable = false) // [✅ NotNull 추가, ERD 기반]
    private String status; // ENUM 타입

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;
}