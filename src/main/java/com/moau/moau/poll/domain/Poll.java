package com.moau.moau.poll.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.notice.domain.Notice;
import com.moau.moau.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "POLLS")
public class Poll extends BaseId {

    // Poll은 Notice에 1:1로 종속되는 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User creator;

    // @Enumerated(EnumType.STRING)
    @Column(name = "result_visibility", nullable = false)
    private String resultVisibility; // 결과 공개 여부

    @Column(name = "closes_at")
    private Instant closesAt; // 투표 마감 시각
}