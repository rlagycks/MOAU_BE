package com.moau.moau.accounting.receipt.service;

import com.moau.moau.accounting.receipt.domain.ReceiptReview;
import com.moau.moau.accounting.receipt.domain.ReviewStatus;
import com.moau.moau.accounting.receipt.dto.response.ReceiptReviewDto;
import com.moau.moau.accounting.receipt.repository.ReceiptReviewRepository;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceiptReviewQueryService {

    private final ReceiptReviewRepository receiptReviewRepository;
    private final UserRepository userRepository;

    // API 4.5: 승인 대기 목록 조회
    public Page<ReceiptReviewDto> getPendingReviews(Long teamId, ReviewStatus status, Pageable pageable) {
        if (status == null) {
            status = ReviewStatus.PENDING_APPROVAL;
        }

        Page<ReceiptReview> reviewPage = receiptReviewRepository.findByTeamIdAndStatus(teamId, status, pageable);

        // Page<Entity> -> Page<DTO> 변환
        return reviewPage.map(review -> {
            String requesterName = userRepository.findById(review.getRequesterId())
                    .map(User::getNickname).orElse("N/A");

            String imageUrl = review.getReceipt().getImageUrl();

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
                    null, null,
                    requesterName,
                    null,
                    null,
                    imageUrl
            );
        });
    }
}