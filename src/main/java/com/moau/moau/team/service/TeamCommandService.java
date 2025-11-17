package com.moau.moau.team.service;

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
    private final TeamMemberService teamMembers; //  추가
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public TeamDetailResponse createTeam(Long ownerUserId, TeamCreateRequest req) {
        User owner = users.findById(ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String inviteCode = generateUniqueInviteCode();

        Team team = TeamFactory.create(owner, req.name(), req.description(), inviteCode);

        Team saved = teams.save(team);

        teamMembers.addOwnerMember(saved.getId(), owner.getId());

        return TeamDetailResponse.from(saved);
    }

    @Transactional
    public TeamDetailResponse updateTeam(Long currentUserId, Long teamId, TeamUpdateRequest req) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (!team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException("팀 수정 권한이 없습니다.");
        }

        team.setName(req.name());
        team.setDescription(req.description());

        return TeamDetailResponse.from(team);
    }

    // TeamCommandService
    @Transactional
    public void deleteTeam(Long currentUserId, Long teamId) {
        Team team = teams.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        if (!team.getOwner().getId().equals(currentUserId)) {
            throw new IllegalStateException("팀 삭제 권한이 없습니다.");
        }

        // 소프트 딜리트: 실제 delete() 안 하고 마킹만
        teams.softDeleteById(teamId, Instant.now());
    }

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
