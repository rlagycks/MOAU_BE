package com.moau.moau.accounting.dues.controller;

import com.moau.moau.accounting.dues.dto.request.DuesStatusUpdateRequestDto;
import com.moau.moau.accounting.dues.dto.response.DuesCycleDetailDto;
import com.moau.moau.accounting.dues.dto.response.DuesCycleDto;
import com.moau.moau.accounting.dues.service.DuesCommandService;
import com.moau.moau.accounting.dues.service.DuesQueryService;
import com.moau.moau.global.payload.ResponseDto;
import com.moau.moau.global.security.CheckTeamRole;
import com.moau.moau.team.domain.TeamMemberRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DuesController implements DuesControllerSwagger {

    private final DuesQueryService duesQueryService;
    private final DuesCommandService duesCommandService;

    @Override
    @CheckTeamRole(TeamMemberRole.ADMIN)
    public ResponseEntity<ResponseDto<List<DuesCycleDto>>> getCycles(
            @PathVariable Long teamId
    ) {
        List<DuesCycleDto> response = duesQueryService.getCycles(teamId);
        return ResponseEntity.ok(ResponseDto.data(response));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.ADMIN)
    public ResponseEntity<ResponseDto<DuesCycleDetailDto>> getCycleStatus(
            @PathVariable Long teamId,
            @RequestParam LocalDate targetDate
    ) {
        DuesCycleDetailDto response = duesQueryService.getCycleStatus(teamId, targetDate);
        return ResponseEntity.ok(ResponseDto.data(response));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.ADMIN)
    public ResponseEntity<ResponseDto<DuesCycleDetailDto>> updateMemberStatus(
            @PathVariable Long teamId,
            @PathVariable Long cycleId,
            @PathVariable Long userId,
            @Valid @RequestBody DuesStatusUpdateRequestDto requestDto
    ) {
        DuesCycleDetailDto response = duesCommandService.updateMemberStatus(teamId, cycleId, userId, requestDto);
        return ResponseEntity.ok(ResponseDto.data(response));
    }
}