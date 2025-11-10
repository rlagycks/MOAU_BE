package com.moau.moau.poll.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.notice.domain.Notice;
import com.moau.moau.user.domain.User;
import jakarta.persistence.*;
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
@Table(name = "POLLS") // [✅ 규칙 통일]
public class Poll extends BaseId {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTICE_ID", nullable = false) // [✅ 대문자 통일]
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", nullable = false) // [✅ 대문자 통일]
    private User creator;

    @Column(name = "result_visibility", nullable = false)
    private String resultVisibility; // ENUM

    @Column(name = "closes_at")
    private Instant closesAt;
}