package com.moau.moau.poll.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor; // [✅ @Builder를 위해 추가]
import lombok.Builder; // [✅ 추가]
import com.moau.moau.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // [✅ @Builder를 위해 추가]
@Builder // [✅ 추가]
@Entity
@Table(name = "POLL_RESPONSES")
public class PollResponse extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLL_ID", nullable = false) // [✅ 대문자 통일]
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONDENT_USER_ID", nullable = false) // [✅ 대문자 통일]
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