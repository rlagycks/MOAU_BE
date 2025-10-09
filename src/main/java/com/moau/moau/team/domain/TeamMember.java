package com.moau.moau.team.domain;

import com.moau.moau.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "GROUP_MEMBERS")
public class TeamMember {

    @EmbeddedId // 위에서 만든 복합키 클래스를 ID로 사용
    private TeamMemberId id;

    @MapsId("groupId") // 복합키의 groupId 필드를 실제 Group 엔티티와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Team team;

    @MapsId("userId") // 복합키의 userId 필드를 실제 User 엔티티와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    // @Enumerated(EnumType.STRING)
    private String role; // ENUM 타입

    @Column(nullable = false)
    // @Enumerated(EnumType.STRING)
    private String status; // ENUM 타입

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // updatedBy는 User 엔티티와 관계를 맺을 수도 있고, Long 타입으로 ID만 저장할 수도 있습니다.
    // 여기서는 ID만 저장하는 방식으로 구현했습니다.
    private Long updatedBy;
}