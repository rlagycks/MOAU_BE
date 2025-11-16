package com.moau.moau.team.repository;

import com.moau.moau.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByOwnerId(Long ownerId);

    boolean existsByInviteCode(String inviteCode);
}
