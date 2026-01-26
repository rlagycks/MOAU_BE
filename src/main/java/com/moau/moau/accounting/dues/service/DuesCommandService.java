package com.moau.moau.accounting.dues.service;

import com.moau.moau.accounting.banking.service.BankingCommandService;
import com.moau.moau.accounting.dues.domain.DuesCycle;
import com.moau.moau.accounting.dues.domain.DuesMemberStatus;
import com.moau.moau.accounting.dues.domain.DuesStatus;
import com.moau.moau.accounting.dues.domain.DuesStatusChangedEvent;
import com.moau.moau.accounting.dues.dto.request.DuesStatusUpdateRequestDto;
import com.moau.moau.accounting.dues.dto.response.DuesCycleDetailDto;
import com.moau.moau.global.exception.error.DuesError;
import com.moau.moau.accounting.dues.repository.DuesCycleRepository;
import com.moau.moau.accounting.dues.repository.DuesMemberStatusRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class DuesCommandService {

    private final DuesCycleRepository duesCycleRepository;
    private final DuesMemberStatusRepository duesMemberStatusRepository;
    private final BankingCommandService bankingCommandService;
    private final DuesQueryService duesQueryService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DuesCycleDetailDto updateMemberStatus(Long teamId, Long cycleId, Long targetUserId, DuesStatusUpdateRequestDto req) {

        // 1. 사이클 확인
        DuesCycle cycle = duesCycleRepository.findByIdAndTeamId(cycleId, teamId)
                .orElseThrow(() -> new BusinessException(DuesError.CYCLE_NOT_FOUND));

        // 2. 멤버 상태 조회
        DuesMemberStatus memberStatus = duesMemberStatusRepository.findByCycleAndUserId(cycle, targetUserId)
                .orElseThrow(() -> new BusinessException(DuesError.MEMBER_STATUS_NOT_FOUND));

        // 3. 변경 사항이 없으면 바로 리턴
        if (memberStatus.getStatus() == req.status()) {
            return duesQueryService.getCycleStatusById(teamId, cycleId);
        }

        String userName = userRepository.findById(targetUserId).map(User::getNickname).orElse("멤버");

        // 4. 이전 상태 저장 (감사 추적용)
        DuesStatus previousStatus = memberStatus.getStatus();
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // 5. 상태 변경 및 뱅킹 연동
        if (req.status() == DuesStatus.PAID) {
            memberStatus.markPaid();

            bankingCommandService.recordIncome(
                    teamId,
                    req.bankAccountId(),
                    memberStatus.getAmount(),
                    req.categoryId(),
                    String.format("%s 회비 납부 (%s)", cycle.getName(), userName),
                    LocalDate.now()
            );
        } else {
            memberStatus.markUnpaid();

            bankingCommandService.recordExpense(
                    teamId,
                    req.bankAccountId(),
                    memberStatus.getAmount(),
                    req.categoryId(),
                    String.format("%s 회비 납부 취소 (%s)", cycle.getName(), userName),
                    LocalDate.now(),
                    null
            );
        }

        // 6. 이벤트 발행 (감사 추적)
        eventPublisher.publishEvent(new DuesStatusChangedEvent(
                cycleId,
                targetUserId,
                previousStatus,
                req.status(),
                memberStatus.getAmount(),
                currentUserId,
                Instant.now(),
                cycle.getName()
        ));

        // 7. 최신 현황 반환 (전체 리스트)
        return duesQueryService.getCycleStatusById(teamId, cycleId);
    }
}