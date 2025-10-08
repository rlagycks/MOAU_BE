package com.moau.moau.group.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable // 이 클래스가 다른 엔티티에 포함될 수 있음을 의미
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode // 복합키는 equals와 hashCode를 구현해야 합니다.
public class GroupMemberId implements Serializable {

    private Long groupId;
    private Long userId;

}