package com.moau.moau.global.security;

import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.team.domain.TeamMemberRole;
import com.moau.moau.team.domain.TeamMember;
import com.moau.moau.team.repository.TeamMemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamRoleInterceptor implements HandlerInterceptor {

    private final TeamMemberRepository teamMemberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 1. @CheckTeamRole 어노테이션 읽기
        CheckTeamRole checkRoleAnnotation = handlerMethod.getMethodAnnotation(CheckTeamRole.class);
        if (checkRoleAnnotation == null) {
            checkRoleAnnotation = handlerMethod.getBeanType().getAnnotation(CheckTeamRole.class);
        }

        if (checkRoleAnnotation == null) {
            return true; // 어노테이션 없으면 검사 패스
        }

        // 2. 어노테이션에서 '필요한 최소 권한' 가져오기
        TeamMemberRole requiredRole = checkRoleAnnotation.value();

        // 3. 사용자 ID 및 팀 ID 가져오기 (기존과 동일)
        Long userId = SecurityUtil.getCurrentUserId();
        Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables == null || !pathVariables.containsKey("teamId")) {
            throw new BusinessException(CommonError.BAD_REQUEST);
        }
        Long teamId = Long.parseLong(pathVariables.get("teamId"));

        // 4. DB에서 '사용자의 실제 권한' 가져오기
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new BusinessException(CommonError.ACCESS_DENIED, "팀 멤버가 아닙니다."));

        TeamMemberRole actualRole = member.getRole();

        // 5. [핵심] 권한 비교
        // '실제 권한'이 '필요한 권한'보다 높거나 같은지 확인
        if (!actualRole.isAtLeast(requiredRole)) {
            log.warn("[Auth] 권한 부족 (userId: {}, teamId: {}, 실제: {}, 필요: {})",
                    userId, teamId, actualRole, requiredRole);
            throw new BusinessException(CommonError.ACCESS_DENIED);
        }

        log.debug("[Auth] 권한 확인 (userId: {}, teamId: {}, role: {})", userId, teamId, actualRole);
        return true;
    }
}