package com.moau.moau.poll.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.notice.domain.Notice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "polls")
public class Poll extends BaseId {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(nullable = false)
    private String title;

    @Column(name = "allow_multiple", nullable = false)
    private boolean allowMultiple;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Builder
    public Poll(Notice notice, String title, boolean allowMultiple, boolean isAnonymous, LocalDate deadline) {
        this.notice = notice;
        this.title = title;
        this.allowMultiple = allowMultiple;
        this.isAnonymous = isAnonymous;
        this.deadline = deadline;
    }

    public boolean isClosed() {
        if (deadline == null) return false;
        return LocalDate.now().isAfter(deadline);
    }
}