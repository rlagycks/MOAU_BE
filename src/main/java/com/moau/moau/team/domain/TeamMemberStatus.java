// src/main/java/com/moau/moau/team/domain/TeamMemberStatus.java
package com.moau.moau.team.domain;

public enum TeamMemberStatus {
    ACTIVE,   // 팀에 소속된 상태
    PENDING,  // 가입 신청만 된 상태 (승인 전)
    LEFT      // 팀에서 나간 상태
}
