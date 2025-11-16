package com.moau.moau.team.repository;

import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    // [✅ 추가]
    // 특정 사용자 ID(userId)를 기준으로, 해당 유저가 속한 모든 팀 멤버 정보를 조회합니다.
    List<TeamMember> findByUserId(Long userId);
}