package com.moau.moau.team.service;

import com.moau.moau.global.exception.error.CommonError;
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

    private TeamMember requireMemberWithRole(Long teamId, Long userId, TeamMemberRole requiredRole) {
        TeamMember member = teamMembers.findActiveMember(teamId, userId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.ACCESS_DENIED.getMessage())
                );

        if (!member.getRole().isAtLeast(requiredRole)) {
            throw new IllegalStateException(CommonError.ACCESS_DENIED.getMessage());
        }
        return member;
    }

    /** 팀 멤버 목록 조회 */
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamsMembers(Long teamId) {

        Team team = teams.findById(teamId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_NOT_FOUND.getMessage())
                );

        return teamMembers.findAllByTeam(team)
                .stream()
                .filter(m -> m.getStatus() == TeamMemberStatus.ACTIVE)
                .map(TeamMemberResponse::from)
                .toList();
    }

    /** 팀 생성 시 OWNER 멤버 추가 */
    @Transactional
    public void addOwnerMember(Long teamId, Long ownerUserId) {

        Team team = teams.findById(teamId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_NOT_FOUND.getMessage())
                );

        User owner = users.findById(ownerUserId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.USER_NOT_FOUND.getMessage())
                );

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

    /** 팀 나가기 */
    @Transactional
    public void leaveTeam(Long teamId) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        requireMemberWithRole(teamId, currentUserId, TeamMemberRole.MEMBER);

        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_NOT_FOUND.getMessage())
                );

        if (team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException(CommonError.TEAM_OWNER_CANNOT_LEAVE.getMessage());
        }

        TeamMemberId id = new TeamMemberId(teamId, currentUserId);

        TeamMember member = teamMembers.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_MEMBER_NOT_FOUND.getMessage())
                );

        if (member.getStatus() == TeamMemberStatus.LEFT) {
            throw new IllegalStateException(CommonError.TEAM_MEMBER_ALREADY_LEFT.getMessage());
        }

        member.setStatus(TeamMemberStatus.LEFT);
        member.setUpdatedBy(currentUserId);
    }

    /** 멤버 역할 변경 */
    @Transactional
    public void changeMemberRole(Long teamId, Long targetUserId, TeamMemberRole newRole) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        requireMemberWithRole(teamId, currentUserId, TeamMemberRole.OWNER);

        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_NOT_FOUND.getMessage())
                );

        if (!TeamMemberRole.ADMIN.equals(newRole) && !TeamMemberRole.MEMBER.equals(newRole)) {
            throw new IllegalStateException(CommonError.TEAM_ROLE_INVALID.getMessage());
        }

        TeamMemberId targetId = new TeamMemberId(teamId, targetUserId);

        TeamMember target = teamMembers.findById(targetId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_MEMBER_NOT_FOUND.getMessage())
                );

        if (target.getStatus() != TeamMemberStatus.ACTIVE) {
            throw new IllegalStateException(CommonError.TEAM_MEMBER_NOT_ACTIVE.getMessage());
        }

        if (TeamMemberRole.OWNER.equals(target.getRole())) {
            throw new IllegalStateException(CommonError.TEAM_OWNER_ROLE_CANNOT_BE_CHANGED.getMessage());
        }

        target.setRole(newRole);
        target.setUpdatedBy(currentUserId);
    }

    /** 팀 강퇴 */
    @Transactional
    public void kickMember(Long teamId, Long targetUserId) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        requireMemberWithRole(teamId, currentUserId, TeamMemberRole.ADMIN);

        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_NOT_FOUND.getMessage())
                );

        TeamMemberId targetId = new TeamMemberId(teamId, targetUserId);

        TeamMember target = teamMembers.findById(targetId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_MEMBER_NOT_FOUND.getMessage())
                );

        if (target.getStatus() == TeamMemberStatus.LEFT) {
            throw new IllegalStateException(CommonError.TEAM_MEMBER_ALREADY_LEFT.getMessage());
        }

        if (TeamMemberRole.OWNER.equals(target.getRole())) {
            throw new IllegalStateException(CommonError.TEAM_OWNER_CANNOT_BE_KICKED.getMessage());
        }

        target.setStatus(TeamMemberStatus.LEFT);
        target.setUpdatedBy(currentUserId);
    }

    /** 팀장 양도 */
    @Transactional
    public void transferOwner(Long teamId, Long newOwnerUserId) {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        requireMemberWithRole(teamId, currentUserId, TeamMemberRole.OWNER);

        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_NOT_FOUND.getMessage())
                );

        User currentOwner = team.getOwner();

        if (currentUserId.equals(newOwnerUserId)) {
            throw new IllegalStateException(CommonError.TEAM_OWNER_TRANSFER_SELF.getMessage());
        }

        User newOwnerUser = users.findById(newOwnerUserId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.USER_NOT_FOUND.getMessage())
                );

        TeamMemberId newOwnerMemberId = new TeamMemberId(teamId, newOwnerUserId);

        TeamMember newOwnerMember = teamMembers.findById(newOwnerMemberId)
                .orElseThrow(() ->
                        new IllegalStateException(CommonError.TEAM_OWNER_TRANSFER_TARGET_NOT_MEMBER.getMessage())
                );

        if (newOwnerMember.getStatus() != TeamMemberStatus.ACTIVE) {
            throw new IllegalStateException(CommonError.TEAM_OWNER_TRANSFER_TARGET_NOT_ACTIVE.getMessage());
        }

        if (TeamMemberRole.OWNER.equals(newOwnerMember.getRole())) {
            throw new IllegalStateException(CommonError.TEAM_OWNER_ALREADY.getMessage());
        }

        TeamMemberId currentOwnerMemberId = new TeamMemberId(teamId, currentUserId);

        TeamMember currentOwnerMember = teamMembers.findById(currentOwnerMemberId)
                .orElseGet(() -> teamMembers.save(
                        TeamMemberFactory.create(
                                team,
                                currentOwner,
                                TeamMemberRole.MEMBER,
                                TeamMemberStatus.ACTIVE,
                                currentUserId
                        )
                ));

        currentOwnerMember.setRole(TeamMemberRole.MEMBER);
        currentOwnerMember.setUpdatedBy(currentUserId);

        newOwnerMember.setRole(TeamMemberRole.OWNER);
        newOwnerMember.setUpdatedBy(currentUserId);

        team.setOwner(newOwnerUser);
    }
}
