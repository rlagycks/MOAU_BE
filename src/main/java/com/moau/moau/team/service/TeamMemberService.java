// src/main/java/com/moau/moau/team/service/TeamMemberService.java
package com.moau.moau.team.service;

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

    /**
     * 팀 멤버 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamsMembers(Long teamId) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        return teamMembers.findAllByTeam(team)
                .stream()
                .filter(m -> m.getStatus() == TeamMemberStatus.ACTIVE) // ACTIVE만
                .map(TeamMemberResponse::from)
                .toList();
    }

    /**
     * 팀 생성 시 오너를 팀 멤버로 추가
     */
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
                TeamMemberStatus.ACTIVE, // 오너는 ACTIVE
                ownerUserId
        );

        teamMembers.save(member);
    }

    /**
     * 팀 나가기 (일반 멤버 전용, 팀장은 나갈 수 없음)
     */
    @Transactional
    public void leaveTeam(Long currentUserId, Long teamId) {
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

    // ===================== 여기서부터 관리자 기능 추가 =====================

    /**
     * [OWNER 전용] 멤버 역할 변경 (승급/강등)
     * - currentUserId: 요청 보낸 사람 (JWT에서 온 유저 id)
     * - targetUserId : 역할을 바꿀 대상 멤버의 유저 id
     * - newRole      : "ADMIN" 또는 "MEMBER" 권장 (문자열 기반)
     */
    @Transactional
    public void changeMemberRole(Long currentUserId, Long teamId, Long targetUserId, TeamMemberRole newRole) {
        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        // 오너만 사용 가능
        if (!team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException("오너만 사용할 수 있는 기능입니다.");
        }

        // 허용되는 역할 값인지 검증 (필요에 따라 수정 가능)
        if (!TeamMemberRole.ADMIN.equals(newRole) && !TeamMemberRole.MEMBER.equals(newRole)) {
            throw new IllegalStateException("유효하지 않은 역할입니다. ADMIN 또는 MEMBER만 설정할 수 있습니다.");
        }

        TeamMemberId targetId = new TeamMemberId(teamId, targetUserId);
        TeamMember target = teamMembers.findById(targetId)
                .orElseThrow(() -> new IllegalStateException("해당 팀의 멤버가 아닙니다."));

        if (target.getStatus() != TeamMemberStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 멤버의 역할은 변경할 수 없습니다.");
        }

        // 오너 역할은 여기서 변경하지 않음 (오너 변경은 transferOwner로만)
        if (TeamMemberRole.OWNER.equals(target.getRole())) {
            throw new IllegalStateException("오너 역할은 이 기능으로 변경할 수 없습니다. 오너 양도 기능을 사용해야 합니다.");
        }

        target.setRole(newRole);
        target.setUpdatedBy(currentUserId);
    }

    /**
     * - currentUserId: 요청 보낸 사람 (오너 ,어드민)
     * - targetUserId : 강퇴할 멤버의 유저 id
     */
    @Transactional
    public void kickMember(Long currentUserId, Long teamId, Long targetUserId) {
        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        // 현재 사용자 멤버 정보 조회
        TeamMemberId actorId = new TeamMemberId(teamId, currentUserId);
        TeamMember actor = teamMembers.findById(actorId)
                .orElseThrow(() -> new IllegalStateException("해당 팀의 멤버가 아닙니다."));

        // ADMIN 또는 OWNER만 강퇴 가능
        TeamMemberRole actorRole = actor.getRole();
        if (actorRole != TeamMemberRole.OWNER && actorRole != TeamMemberRole.ADMIN) {
            throw new IllegalStateException("관리자 또는 오너만 사용할 수 있는 기능입니다.");
        }

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

    /**
     * [OWNER 전용] 오너 양도
     * - currentUserId: 현재 오너(요청자)
     * - newOwnerUserId: 새 오너가 될 유저 id (팀 멤버여야 함)
     *
     * 규칙:
     * - 호출자는 반드시 현재 팀 오너여야 함
     * - 양도 대상은 해당 팀의 ACTIVE 멤버여야 함
     * - 기존 오너는 MEMBER로 내려감
     * - 새 오너는 OWNER 역할로 변경
     */
    @Transactional
    public void transferOwner(Long currentUserId, Long teamId, Long newOwnerUserId) {
        Team team = teams.findByIdAndDeletedAtIsNull(teamId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 팀입니다."));

        User currentOwner = team.getOwner();

        if (!currentOwner.getId().equals(currentUserId)) {
            throw new IllegalStateException("오너만 사용할 수 있는 기능입니다.");
        }

        if (currentUserId.equals(newOwnerUserId)) {
            throw new IllegalStateException("자기 자신에게는 오너를 양도할 수 없습니다.");
        }

        User newOwnerUser = users.findById(newOwnerUserId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        // 새 오너가 팀 멤버인지 확인
        TeamMemberId newOwnerMemberId = new TeamMemberId(teamId, newOwnerUserId);
        TeamMember newOwnerMember = teamMembers.findById(newOwnerMemberId)
                .orElseThrow(() -> new IllegalStateException("오너로 양도할 대상이 팀 멤버가 아닙니다."));

        if (newOwnerMember.getStatus() != TeamMemberStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 멤버에게는 오너를 양도할 수 없습니다.");
        }

        if (TeamMemberRole.OWNER.equals(newOwnerMember.getRole())) {
            throw new IllegalStateException("이미 오너 권한을 가진 멤버입니다.");
        }

        // 기존 오너 멤버 레코드 조회 (없으면 생성)
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

        // 역할 변경: 기존 오너 → MEMBER, 새 오너 → OWNER
        currentOwnerMember.setRole(TeamMemberRole.MEMBER);
        currentOwnerMember.setUpdatedBy(currentUserId);

        newOwnerMember.setRole(TeamMemberRole.OWNER);
        newOwnerMember.setUpdatedBy(currentUserId);

        // Team 엔티티의 owner 필드도 변경
        team.setOwner(newOwnerUser);
    }
}
