package com.moau.moau.team.service;

import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.team.domain.Team;
import com.moau.moau.team.domain.TeamFactory;
import com.moau.moau.team.repository.TeamRepository;
import com.moau.moau.team.dto.request.TeamCreateRequest;
import com.moau.moau.team.dto.request.TeamUpdateRequest;
import com.moau.moau.team.dto.response.TeamDetailResponse;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TeamCommandService {

    private final TeamRepository teams;
    private final UserRepository users;
    private final TeamMemberService teamMembers;
    private final SecureRandom random = new SecureRandom();

    /** 팀 생성 */
    @Transactional
    public TeamDetailResponse createTeam(Long ownerUserId, TeamCreateRequest req) {

        User owner = users.findById(ownerUserId)
                .orElseThrow(() ->
                        new IllegalArgumentException(CommonError.USER_NOT_FOUND.getMessage())
                );

        String inviteCode = generateUniqueInviteCode();

        Team team = TeamFactory.create(owner, req.name(), req.description(), inviteCode);

        Team saved = teams.save(team);

        // 팀장 멤버 추가
        teamMembers.addOwnerMember(saved.getId(), owner.getId());

        return TeamDetailResponse.from(saved);
    }

    /** 팀 수정 */
    @Transactional
    public TeamDetailResponse updateTeam(Long currentUserId, Long teamId, TeamUpdateRequest req) {

        Team team = teams.findById(teamId)
                .orElseThrow(() ->
                        new IllegalArgumentException(CommonError.TEAM_NOT_FOUND.getMessage())
                );

        if (!team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException(CommonError.TEAM_UPDATE_FORBIDDEN.getMessage());
        }

        team.setName(req.name());
        team.setDescription(req.description());

        return TeamDetailResponse.from(team);
    }

    /** 팀 삭제 (Soft Delete) */
    @Transactional
    public void deleteTeam(Long currentUserId, Long teamId) {

        Team team = teams.findById(teamId)
                .orElseThrow(() ->
                        new IllegalArgumentException(CommonError.TEAM_NOT_FOUND.getMessage())
                );

        if (!team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException(CommonError.TEAM_DELETE_FORBIDDEN.getMessage());
        }

        teams.softDeleteById(teamId, Instant.now());
    }

    /** 초대 코드 생성 */
    private String generateUniqueInviteCode() {
        String code;
        do {
            code = generateInviteCode();
        } while (teams.existsByInviteCodeAndDeletedAtIsNull(code));
        return code;
    }

    private String generateInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
