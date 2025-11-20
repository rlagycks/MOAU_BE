package com.moau.moau.accounting.receipt.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PresignedUrlRequestDto(
        @NotBlank(message = "파일 이름은 필수입니다.")
        String filename
) {
}