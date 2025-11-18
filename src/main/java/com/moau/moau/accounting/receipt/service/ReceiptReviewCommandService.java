package com.moau.moau.accounting.receipt.service;

import com.moau.moau.accounting.banking.service.BankingCommandService;
import com.moau.moau.accounting.receipt.domain.Receipt;
import com.moau.moau.accounting.receipt.domain.ReceiptReview;
import com.moau.moau.accounting.receipt.domain.ReviewStatus;
import com.moau.moau.accounting.receipt.dto.request.ApproveReceiptRequestDto;
import com.moau.moau.accounting.receipt.dto.request.RejectReceiptRequestDto;
import com.moau.moau.accounting.receipt.dto.request.RequestReviewRequestDto;
import com.moau.moau.accounting.receipt.dto.response.ReceiptReviewDto;
import com.moau.moau.global.exception.error.ReceiptError;
import com.moau.moau.accounting.receipt.repository.ReceiptRepository;
import com.moau.moau.accounting.receipt.repository.ReceiptReviewRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.security.SecurityUtil;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReceiptReviewCommandService {

    private final ReceiptReviewRepository receiptReviewRepository;
    private final ReceiptRepository receiptRepository;
    private final BankingCommandService bankingCommandService; // (뱅킹 도메인 주입)
    private final UserRepository userRepository;

    // API 4.4: 관리자 승인 요청 생성
    public ReceiptReviewDto requestReview(Long teamId, Long receiptId, RequestReviewRequestDto dto) {
        Long requesterId = SecurityUtil.getCurrentUserId();

        Receipt receipt = receiptRepository.findByIdAndTeamId(receiptId, teamId)
                .orElseThrow(() -> new BusinessException(ReceiptError.RECEIPT_NOT_FOUND));

        // (이미 처리 중/완료된 요청이 있는지 확인)
        if (receiptReviewRepository.existsByReceiptIdAndStatusIn(receiptId,
                java.util.List.of(ReviewStatus.PENDING_APPROVAL, ReviewStatus.APPROVED))) {
            throw new BusinessException(ReceiptError.ALREADY_REVIEW_REQUESTED);
        }

        ReceiptReview review = ReceiptReview.builder()
                .receipt(receipt)
                .teamId(teamId)
                .requesterId(requesterId)
                .amountCents(dto.amountCents())
                .transactionDate(dto.transactionDate())
                .merchantName(dto.merchantName())
                .paymentMethod(dto.paymentMethod())
                .description(dto.description())
                .build();

        ReceiptReview savedReview = receiptReviewRepository.save(review);
        return mapToReviewDto(savedReview, null); // (DTO 변환)
    }

    // API 4.6: 영수증 최종 승인
    public ReceiptReviewDto approveReceipt(Long teamId, Long reviewId, ApproveReceiptRequestDto dto) {
        Long approverId = SecurityUtil.getCurrentUserId();

        ReceiptReview review = receiptReviewRepository.findByIdAndTeamId(reviewId, teamId)
                .orElseThrow(() -> new BusinessException(ReceiptError.RECEIPT_REVIEW_NOT_FOUND));

        if (review.getStatus() != ReviewStatus.PENDING_APPROVAL) {
            throw new BusinessException(ReceiptError.ALREADY_REVIEW_COMPLETED);
        }

        // 1. 상태 변경
        review.approve(approverId);

        // 2. [핵심] 뱅킹 도메인 호출 (지출 기록)
        bankingCommandService.recordExpense(
                teamId,
                dto.bankAccountId(),
                review.getAmountCents(),
                dto.categoryId(),
                review.getDescription(),
                review.getTransactionDate(),
                reviewId
        );

        return mapToReviewDto(review, null); // (DTO 변환)
    }

    // API 4.7: 영수증 반려
    public ReceiptReviewDto rejectReceipt(Long teamId, Long reviewId, RejectReceiptRequestDto dto) {
        Long approverId = SecurityUtil.getCurrentUserId();

        ReceiptReview review = receiptReviewRepository.findByIdAndTeamId(reviewId, teamId)
                .orElseThrow(() -> new BusinessException(ReceiptError.RECEIPT_REVIEW_NOT_FOUND));

        if (review.getStatus() != ReviewStatus.PENDING_APPROVAL) {
            throw new BusinessException(ReceiptError.ALREADY_REVIEW_COMPLETED);
        }

        // 1. 상태 변경
        review.reject(approverId, dto.reason());

        return mapToReviewDto(review, null); // (DTO 변환)
    }

    // --- Helper Method ---
    // (DTO 변환 로직 - 중복 제거)
    private ReceiptReviewDto mapToReviewDto(ReceiptReview review, String receiptImageUrl) {
        String requesterName = userRepository.findById(review.getRequesterId())
                .map(User::getNickname).orElse("N/A");
        String approverName = Optional.ofNullable(review.getApproverId())
                .flatMap(userRepository::findById)
                .map(User::getNickname).orElse(null);

        return new ReceiptReviewDto(
                review.getId(),
                review.getReceipt().getId(),
                review.getStatus().name(),
                review.getAmountCents(),
                review.getTransactionDate(),
                review.getMerchantName(),
                review.getPaymentMethod(),
                review.getDescription(),
                review.getRequestedAt(),
                review.getApprovedAt(),
                review.getRejectedAt(),
                requesterName,
                approverName,
                review.getRejectReason(),
                receiptImageUrl // (목록 조회 시에만 사용됨)
        );
    }
}