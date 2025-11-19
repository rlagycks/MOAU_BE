package com.moau.moau.accounting.common.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_team_category_name_type",
                        columnNames = {"team_id", "name", "type"}
                )
        }
)
public class Category extends BaseId {

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type; // INCOME, EXPENSE

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Builder
    public Category(Long teamId, String name, TransactionType type) {
        this.teamId = teamId;
        this.name = name;
        this.type = type;
        this.isActive = true; // 생성 시 기본 활성
    }

    // 수정용 편의 메서드
    public void update(String name, boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }
}