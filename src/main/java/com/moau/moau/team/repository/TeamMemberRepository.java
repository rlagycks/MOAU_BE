package com.moau.moau.team.repository;

import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.domain.TeamMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

}