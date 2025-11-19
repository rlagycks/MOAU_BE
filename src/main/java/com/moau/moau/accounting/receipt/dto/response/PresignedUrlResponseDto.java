package com.moau.moau.accounting.receipt.dto.response;

public record PresignedUrlResponseDto(
        String uploadUrl,
        String s3Key
) {
}