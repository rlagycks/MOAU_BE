package com.moau.moau.accounting.receipt.service;

import com.moau.moau.accounting.receipt.port.StorageServicePort;
import com.moau.moau.accounting.receipt.domain.Receipt;
import com.moau.moau.accounting.receipt.domain.ReceiptReview;
import com.moau.moau.accounting.receipt.domain.ReviewStatus;
import com.moau.moau.accounting.receipt.dto.response.ReceiptDetailDto;
import com.moau.moau.global.exception.error.ReceiptError;
import com.moau.moau.accounting.receipt.repository.ReceiptRepository;
import com.moau.moau.accounting.receipt.repository.ReceiptReviewRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.user.domain.User;
import com.moau.moau.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceiptQueryService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptReviewRepository receiptReviewRepository;
    private final UserRepository userRepository;
    private final StorageServicePort storageServicePort; // ⬅️ [1] 주입

    public ReceiptDetailDto getReceiptDetail(Long teamId, Long receiptId) {
        Receipt receipt = receiptRepository.findByIdAndTeamId(receiptId, teamId)
                .orElseThrow(() -> new BusinessException(ReceiptError.RECEIPT_NOT_FOUND));

        // ⬇️ [2] S3 Key -> 임시 조회 URL(5분) 변환
        String presignedImageUrl = storageServicePort.createPresignedGetUrl(receipt.getS3Key());

        // ⬇️ [3] OCR 결과 DTO 빌드 (paymentMethod 포함)
        ReceiptDetailDto.OcrResultDto ocrResultDto = new ReceiptDetailDto.OcrResultDto(
                receipt.getOcrMerchantName(),
                receipt.getOcrAmountCents(),
                receipt.getOcrTransactionDate(),
                receipt.getOcrPaymentMethod() // ⬅️ 엔티티에서 가져옴
        );

        Optional<ReceiptReview> reviewOpt = receiptReviewRepository.findByReceiptId(receiptId);

        if (reviewOpt.isEmpty()) {
            return new ReceiptDetailDto(
                    receipt.getId(),
                    presignedImageUrl, // ⬅️ 임시 URL 사용
                    receipt.getDescription(),
                    receipt.getOcrStatus().name(),
                    ocrResultDto,
                    null, ReviewStatus.PENDING_REVIEW.name(), null, null, null,
                    null, null, null, null, null, null, null
            );
        } else {
            ReceiptReview review = reviewOpt.get();
            String requesterName = userRepository.findById(review.getRequesterId())
                    .map(User::getNickname).orElse("N/A");
            String approverName = Optional.ofNullable(review.getApproverId())
                    .flatMap(userRepository::findById)
                    .map(User::getNickname).orElse(null);

            return new ReceiptDetailDto(
                    receipt.getId(),
                    presignedImageUrl, // ⬅️ 임시 URL 사용
                    receipt.getDescription(),
                    receipt.getOcrStatus().name(),
                    ocrResultDto,
                    review.getId(),
                    review.getStatus().name(),
                    review.getAmountCents(),
                    review.getTransactionDate(),
                    review.getDescription(),
                    review.getPaymentMethod(),
                    requesterName,
                    approverName,
                    review.getRequestedAt(),
                    review.getApprovedAt(),
                    review.getRejectedAt(),
                    review.getRejectReason()
            );
        }
    }
}