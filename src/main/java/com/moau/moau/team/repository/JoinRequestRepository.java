// src/main/java/com/moau/moau/team/domain/repository/JoinRequestRepository.java
package com.moau.moau.team.repository;

import com.moau.moau.team.domain.JoinRequest;
import com.moau.moau.team.domain.Team;
import com.moau.moau.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {

    boolean existsByTeamAndRequestUserAndStatus(Team team, User requestUser, String status);
}
