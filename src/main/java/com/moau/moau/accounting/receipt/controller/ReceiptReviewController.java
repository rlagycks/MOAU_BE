package com.moau.moau.accounting.receipt.controller;

import com.moau.moau.accounting.receipt.domain.ReviewStatus;
import com.moau.moau.accounting.receipt.dto.request.ApproveReceiptRequestDto;
import com.moau.moau.accounting.receipt.dto.request.RejectReceiptRequestDto;
import com.moau.moau.accounting.receipt.dto.response.ReceiptReviewDto;
import com.moau.moau.accounting.receipt.service.ReceiptReviewCommandService;
import com.moau.moau.accounting.receipt.service.ReceiptReviewQueryService;
import com.moau.moau.global.payload.ResponseDto;
import com.moau.moau.global.security.CheckTeamRole;
import com.moau.moau.team.domain.TeamMemberRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams/{teamId}/accounting/reviews/receipts")
@RequiredArgsConstructor
public class ReceiptReviewController implements ReceiptReviewApi {

    private final ReceiptReviewCommandService receiptReviewCommandService;
    private final ReceiptReviewQueryService receiptReviewQueryService;

    @Override
    @GetMapping
    public ResponseEntity<ResponseDto<Page<ReceiptReviewDto>>> getPendingReviews(
            @PathVariable Long teamId,
            @RequestParam(required = false, defaultValue = "PENDING_APPROVAL") ReviewStatus status,
            @PageableDefault(sort = "requestedAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable
    ) {
        Page<ReceiptReviewDto> responseDto = receiptReviewQueryService.getPendingReviews(teamId, status, pageable);
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.ADMIN)
    @PostMapping("/{reviewId}/approve")
    public ResponseEntity<ResponseDto<ReceiptReviewDto>> approveReceipt(
            @PathVariable Long teamId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ApproveReceiptRequestDto requestDto
    ) {
        ReceiptReviewDto responseDto = receiptReviewCommandService.approveReceipt(teamId, reviewId, requestDto);
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }

    @Override
    @CheckTeamRole(TeamMemberRole.ADMIN)
    @PostMapping("/{reviewId}/reject")
    public ResponseEntity<ResponseDto<ReceiptReviewDto>> rejectReceipt(
            @PathVariable Long teamId,
            @PathVariable Long reviewId,
            @Valid @RequestBody RejectReceiptRequestDto requestDto
    ) {
        ReceiptReviewDto responseDto = receiptReviewCommandService.rejectReceipt(teamId, reviewId, requestDto);
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }
}