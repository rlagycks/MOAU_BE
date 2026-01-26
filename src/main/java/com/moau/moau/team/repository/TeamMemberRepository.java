// src/main/java/com/moau/moau/team/repository/TeamMemberRepository.java
package com.moau.moau.team.repository;

import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberId;
import com.moau.moau.team.domain.TeamMemberStatus;
import com.moau.moau.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    boolean existsByTeamAndUser(Team team, User user);

    List<TeamMember> findByUserId(Long userId);

    /**
     * N+1 쿼리 방지: Team과 함께 조회
     */
    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.team WHERE tm.id.userId = :userId")
    List<TeamMember> findByUserIdWithTeam(Long userId);

    List<TeamMember> findAllByTeam(Team team);

    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);
    boolean existsByTeamIdAndUserId(Long teamId, Long userId);
    boolean existsByTeam_IdAndUser_IdAndStatus(Long teamId, Long userId, TeamMemberStatus status);

    /**
     * 팀 소프트 삭제 여부까지 고려해 ACTIVE 멤버를 조회한다.
     */
    @Query("""
            select tm
            from TeamMember tm
            join fetch tm.team t
            where tm.id.teamId = :teamId
              and tm.id.userId = :userId
              and tm.status = 'ACTIVE'
              and t.deletedAt is null
            """)
    Optional<TeamMember> findActiveMember(Long teamId, Long userId);
}
