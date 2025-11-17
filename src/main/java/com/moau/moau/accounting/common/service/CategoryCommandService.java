package com.moau.moau.accounting.common.service;

import com.moau.moau.accounting.common.domain.Category;
import com.moau.moau.accounting.common.dto.request.CategoryRequestDto;
import com.moau.moau.accounting.common.dto.response.CategoryResponseDto;
import com.moau.moau.accounting.common.exception.CategoryError; // (새로 임포트)
import com.moau.moau.accounting.common.repository.CategoryRepository;
import com.moau.moau.global.exception.BusinessException;
import com.moau.moau.global.exception.error.CommonError;
import com.moau.moau.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final TeamRepository teamRepository;

    public CategoryResponseDto createCategory(Long teamId, CategoryRequestDto dto) {
        if (!teamRepository.existsById(teamId)) {
            throw new BusinessException(CommonError.TEAM_NOT_FOUND);
        }

        if (categoryRepository.existsByTeamIdAndNameAndType(teamId, dto.name(), dto.type())) {
            throw new BusinessException(CategoryError.NAME_DUPLICATED);
        }

        Category newCategory = Category.builder()
                .teamId(teamId)
                .name(dto.name())
                .type(dto.type())
                .build();

        categoryRepository.save(newCategory);
        return CategoryResponseDto.from(newCategory);
    }

    public CategoryResponseDto updateCategory(Long teamId, Long categoryId, CategoryRequestDto dto) {
        Category category = categoryRepository.findByIdAndTeamId(categoryId, teamId)
                .orElseThrow(() -> new BusinessException(CategoryError.NOT_FOUND));

        if (categoryRepository.existsByTeamIdAndNameAndTypeAndIdNot(teamId, dto.name(), dto.type(), categoryId)) {
            throw new BusinessException(CategoryError.NAME_DUPLICATED);
        }

        category.update(dto.name(), dto.isActive());
        return CategoryResponseDto.from(category);
    }
}