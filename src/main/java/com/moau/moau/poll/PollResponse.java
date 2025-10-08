package com.moau.moau.poll;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "POLL_RESPONSES")
public class PollResponse extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondent_user_id", nullable = false)
    private User respondent;

    @Column(name = "anon_identity", length = 8)
    private String anonIdentity;

    // @Enumerated(EnumType.STRING)
    @Column(name = "response_type")
    private String responseType;

    @Column(name = "value_text", columnDefinition = "TEXT")
    private String valueText;

    @Column(name = "value_bool")
    private Boolean valueBool;

    @Column(name = "value_int")
    private Integer valueInt;
}