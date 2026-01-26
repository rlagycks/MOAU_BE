package com.moau.moau.accounting.receipt.service;

import com.moau.moau.accounting.receipt.port.StorageServicePort;
import com.moau.moau.accounting.receipt.domain.Receipt;
import com.moau.moau.accounting.receipt.domain.ReviewStatus;
import com.moau.moau.accounting.receipt.domain.ReceiptCreatedEvent;
import com.moau.moau.accounting.receipt.dto.request.ReceiptCreateRequestDto;
import com.moau.moau.accounting.receipt.dto.response.PresignedUrlResponseDto;
import com.moau.moau.accounting.receipt.dto.response.ReceiptDto;
import com.moau.moau.accounting.receipt.repository.ReceiptRepository;
import com.moau.moau.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReceiptCommandService {
    private final ReceiptRepository receiptRepository;
    private final StorageServicePort storageServicePort;
    private final ApplicationEventPublisher eventPublisher;

    public PresignedUrlResponseDto createPresignedUrl(String filename) {
        return storageServicePort.createPresignedUrl(filename);
    }

    // API 4.2: 영수증 정보 등록
    public ReceiptDto createReceipt(Long teamId, ReceiptCreateRequestDto dto) {
        Long uploaderId = SecurityUtil.getCurrentUserId();

        String imageUrl = storageServicePort.getObjectUrl(dto.s3Key());

        Receipt receipt = Receipt.builder()
                .teamId(teamId)
                .uploaderId(uploaderId)
                .s3Key(dto.s3Key())
                .imageUrl(imageUrl)
                .description(dto.description())
                .build();

        Receipt savedReceipt = receiptRepository.save(receipt);

        eventPublisher.publishEvent(new ReceiptCreatedEvent(savedReceipt.getId()));

        // N+1 쿼리 방지: 영수증 생성 응답에서 uploaderName 제거 (프론트엔드에서 현재 사용자 정보로 처리)
        return new ReceiptDto(
                savedReceipt.getId(),
                null,  // uploaderName은 프론트엔드에서 현재 사용자 정보로 표시
                savedReceipt.getImageUrl(),
                savedReceipt.getDescription(),
                savedReceipt.getOcrStatus().name(),
                ReviewStatus.PENDING_REVIEW.name(),
                savedReceipt.getCreatedAt()
        );
    }
}