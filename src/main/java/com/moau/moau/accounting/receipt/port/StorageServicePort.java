package com.moau.moau.accounting.receipt.port;

import com.moau.moau.accounting.receipt.dto.response.PresignedUrlResponseDto;

public interface StorageServicePort {
    // S3 Pre-signed URL 생성
    PresignedUrlResponseDto createPresignedUrl(String filename);
    // S3 Key로 영구 URL 조회
    String getObjectUrl(String s3Key);
    String createPresignedGetUrl(String s3Key);
}