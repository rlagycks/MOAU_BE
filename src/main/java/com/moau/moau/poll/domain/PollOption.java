package com.moau.moau.poll.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "poll_options")
public class PollOption extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(nullable = false)
    private String text;

    @Column(name = "vote_count", nullable = false)
    private int voteCount = 0;

    @Builder
    public PollOption(Poll poll, String text) {
        this.poll = poll;
        this.text = text;
        this.voteCount = 0;
    }

    public void increaseVote() {
        this.voteCount++;
    }

    public void decreaseVote() {
        if (this.voteCount > 0) {
            this.voteCount--;
        }
    }
}