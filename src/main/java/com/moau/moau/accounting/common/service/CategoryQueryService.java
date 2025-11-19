package com.moau.moau.accounting.common.service;

import com.moau.moau.accounting.common.domain.TransactionType;
import com.moau.moau.accounting.common.dto.response.CategoryResponseDto;
import com.moau.moau.accounting.common.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDto> getCategories(Long teamId, TransactionType type) {
        if (type == null) {
            // (전체 조회)
            return categoryRepository.findByTeamId(teamId).stream()
                    .map(CategoryResponseDto::from)
                    .collect(Collectors.toList());
        } else {
            // (타입별 필터링 조회)
            return categoryRepository.findByTeamIdAndType(teamId, type).stream()
                    .map(CategoryResponseDto::from)
                    .collect(Collectors.toList());
        }
    }
}