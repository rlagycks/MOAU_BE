package com.moau.moau.team.service;

import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.team.domain.*;
import com.moau.moau.team.dto.response.TeamMemberResponse;
import com.moau.moau.team.repository.TeamMemberRepository;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamRepository teams;
    private final TeamMemberRepository teamMembers;
    private final UserRepository users;


    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamsMembers(Long teamId) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        return teamMembers.findAllByTeam(team)
                .stream()
                .filter(m -> m.getStatus() == TeamMemberStatus.ACTIVE)
                .map(TeamMemberResponse::from)
                .toList();
    }

    @Transactional
    public void addOwnerMember(Long teamId, Long ownerUserId) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        User owner = users.findById(ownerUserId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        TeamMemberId id = new TeamMemberId(teamId, ownerUserId);

        if (teamMembers.existsById(id)) {
            return;
        }

        TeamMember member = TeamMemberFactory.create(
                team,
                owner,
                TeamMemberRole.OWNER,
                TeamMemberStatus.ACTIVE,
                ownerUserId
        );

        teamMembers.save(member);
    }

    @Transactional
    public void leaveTeam(Long teamId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        if (team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException("대표는 팀을 나갈 수 없습니다. 팀을 삭제하거나 팀장을 다른 멤버에게 양도해야 합니다.");
        }

        TeamMemberId id = new TeamMemberId(teamId, currentUserId);
        TeamMember member = teamMembers.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 팀의 멤버가 아닙니다."));

        if (member.getStatus() == TeamMemberStatus.LEFT) {
            throw new IllegalStateException("이미 팀을 나간 멤버입니다.");
        }

        member.setStatus(TeamMemberStatus.LEFT);
        member.setUpdatedBy(currentUserId);
    }

    @Transactional
    public void changeMemberRole(Long teamId, Long targetUserId, TeamMemberRole newRole) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        if (!TeamMemberRole.ADMIN.equals(newRole) && !TeamMemberRole.MEMBER.equals(newRole)) {
            throw new IllegalStateException("유효하지 않은 역할입니다. ADMIN 또는 MEMBER만 설정할 수 있습니다.");
        }

        TeamMemberId targetId = new TeamMemberId(teamId, targetUserId);
        TeamMember target = teamMembers.findById(targetId)
                .orElseThrow(() -> new IllegalStateException("해당 팀의 멤버가 아닙니다."));

        if (target.getStatus() != TeamMemberStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 멤버의 역할은 변경할 수 없습니다.");
        }

        if (TeamMemberRole.OWNER.equals(target.getRole())) {
            throw new IllegalStateException("오너 역할은 이 기능으로 변경할 수 없습니다.");
        }

        target.setRole(newRole);
        target.setUpdatedBy(currentUserId);
    }

    @Transactional
    public void kickMember(Long teamId, Long targetUserId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        TeamMemberId targetId = new TeamMemberId(teamId, targetUserId);
        TeamMember target = teamMembers.findById(targetId)
                .orElseThrow(() -> new IllegalStateException("해당 팀의 멤버가 아닙니다."));

        if (target.getStatus() == TeamMemberStatus.LEFT) {
            throw new IllegalStateException("이미 팀을 나간 멤버입니다.");
        }

        if (TeamMemberRole.OWNER.equals(target.getRole())) {
            throw new IllegalStateException("팀 소유자는 강퇴할 수 없습니다.");
        }

        target.setStatus(TeamMemberStatus.LEFT);
        target.setUpdatedBy(currentUserId);
    }

    @Transactional
    public void transferOwner(Long teamId, Long newOwnerUserId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        User currentOwner = team.getOwner();

        if (currentUserId.equals(newOwnerUserId)) {
            throw new IllegalStateException("자기 자신에게는 오너를 양도할 수 없습니다.");
        }

        User newOwnerUser = users.findById(newOwnerUserId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        TeamMemberId newOwnerMemberId = new TeamMemberId(teamId, newOwnerUserId);
        TeamMember newOwnerMember = teamMembers.findById(newOwnerMemberId)
                .orElseThrow(() -> new IllegalStateException("오너로 양도할 대상이 팀 멤버가 아닙니다."));

        if (newOwnerMember.getStatus() != TeamMemberStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 멤버에게는 오너를 양도할 수 없습니다.");
        }

        if (TeamMemberRole.OWNER.equals(newOwnerMember.getRole())) {
            throw new IllegalStateException("이미 오너 권한을 가진 멤버입니다.");
        }

        TeamMemberId currentOwnerMemberId = new TeamMemberId(teamId, currentUserId);
        TeamMember currentOwnerMember = teamMembers.findById(currentOwnerMemberId)
                .orElseGet(() -> {
                    TeamMember created = TeamMemberFactory.create(
                            team,
                            currentOwner,
                            TeamMemberRole.MEMBER,
                            TeamMemberStatus.ACTIVE,
                            currentUserId
                    );
                    return teamMembers.save(created);
                });

        currentOwnerMember.setRole(TeamMemberRole.MEMBER);
        currentOwnerMember.setUpdatedBy(currentUserId);

        newOwnerMember.setRole(TeamMemberRole.OWNER);
        newOwnerMember.setUpdatedBy(currentUserId);

        team.setOwner(newOwnerUser);
    }
}