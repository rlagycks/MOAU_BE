// src/main/java/com/moau/moau/team/repository/TeamMemberRepository.java
package com.moau.moau.team.repository;

import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberId;
import com.moau.moau.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    boolean existsByTeamAndUser(Team team, User user);

    Optional<TeamMember> findByTeamAndUser(Team team, User user);

    List<TeamMember> findAllByTeam(Team team);
}
