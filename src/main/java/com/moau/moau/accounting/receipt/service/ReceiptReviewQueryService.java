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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceiptReviewQueryService {

    private final ReceiptReviewRepository receiptReviewRepository;
    private final UserRepository userRepository;

    public Page<ReceiptReviewDto> getReceiptReviews(Long teamId, ReviewStatus status, Pageable pageable) {

        Page<ReceiptReview> reviewPage;

        if (status == null) {
            reviewPage = receiptReviewRepository.findByTeamId(teamId, pageable);
        } else {
            reviewPage = receiptReviewRepository.findByTeamIdAndStatus(teamId, status, pageable);
        }

        // N+1 쿼리 방지: requester ID 수집 후 일괄 조회
        Set<Long> requesterIds = reviewPage.stream()
                .map(ReceiptReview::getRequesterId)
                .collect(Collectors.toSet());

        Map<Long, String> userNicknameMap = userRepository.findAllByIdIn(requesterIds).stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));

        // Page<Entity> -> Page<DTO> 변환
        return reviewPage.map(review -> {
            String requesterName = userNicknameMap.getOrDefault(review.getRequesterId(), "N/A");
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