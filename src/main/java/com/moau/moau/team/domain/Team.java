package com.moau.moau.team.domain;

import com.moau.moau.global.domain.BaseId;
import com.moau.moau.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "`GROUPS`") // <-- 이 부분을 수정!
public class Team extends BaseId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "invite_code", length = 8, unique = true)
    private String inviteCode;
}