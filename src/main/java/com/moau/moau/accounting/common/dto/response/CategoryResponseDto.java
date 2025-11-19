package com.moau.moau.accounting.common.dto.response;

import com.moau.moau.accounting.common.domain.Category;
import com.moau.moau.accounting.common.domain.TransactionType;

public record CategoryResponseDto(
        Long categoryId,
        String name,
        TransactionType type,
        boolean isActive
) {
    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getType(),
                category.isActive()
        );
    }
}