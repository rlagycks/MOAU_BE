package com.moau.moau.accounting.receipt.domain;

import com.moau.moau.global.domain.BaseId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "receipts")
public class Receipt extends BaseId {

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;

    @Column(name = "s3_key", nullable = false, unique = true)
    private String s3Key;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "ocr_status", nullable = false, length = 20)
    private OcrStatus ocrStatus;

    // --- OCR Result Fields ---
    @Column(name = "ocr_merchant_name")
    private String ocrMerchantName;

    @Column(name = "ocr_amount_cents")
    private Long ocrAmountCents;

    @Column(name = "ocr_transaction_date")
    private LocalDate ocrTransactionDate;

    @Column(name = "ocr_payment_method")
    private String ocrPaymentMethod;

    @Builder
    public Receipt(Long teamId, Long uploaderId, String s3Key, String imageUrl, String description) {
        this.teamId = teamId;
        this.uploaderId = uploaderId;
        this.s3Key = s3Key;
        this.imageUrl = imageUrl;
        this.description = description;
        this.ocrStatus = OcrStatus.PENDING;
    }

    public void updateOcrResult(OcrStatus status, String merchant, Long amount, LocalDate date, String paymentMethod) {
        this.ocrStatus = status;
        this.ocrMerchantName = merchant;
        this.ocrAmountCents = amount;
        this.ocrTransactionDate = date;
        this.ocrPaymentMethod = paymentMethod; // 저장
    }
}