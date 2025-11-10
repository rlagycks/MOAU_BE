package com.moau.moau.team.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class TeamMemberId implements Serializable {

    @Column(name = "TEAM_ID") // [✅ 수정] 대문자
    private Long teamId;

    @Column(name = "USER_ID") // [✅ 수정] 대문자
    private Long userId;

}