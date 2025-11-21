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
@Table(name = "poll_votes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"poll_option_id", "user_id"})
        }
)
public class PollVote extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_option_id", nullable = false)
    private PollOption pollOption;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    public PollVote(Poll poll, PollOption pollOption, Long userId) {
        this.poll = poll;
        this.pollOption = pollOption;
        this.userId = userId;
    }
}