package com.moau.moau.team.repository;

import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    // [기존] 특정 사용자 ID(userId)를 기준으로, 해당 유저가 속한 모든 팀 멤버 정보를 조회합니다.
    List<TeamMember> findByUserId(Long userId);

    // [✅ 추가] 특정 팀(Team ID)과 특정 유저(User ID)가 매칭되는 멤버가 존재하는지 확인합니다.
    // existsByTeam_IdAndUser_Id: TeamMember 엔티티 내부의 Team 객체(Team_Id)와
    // User 객체(User_Id)를 참조하여 필터링합니다.
    boolean existsByTeam_IdAndUser_Id(Long teamId, Long userId);
}