package com.moau.moau.accounting.common.controller;

import com.moau.moau.accounting.common.domain.TransactionType;
import com.moau.moau.accounting.common.dto.request.CategoryRequestDto;
import com.moau.moau.accounting.common.dto.response.CategoryResponseDto;
import com.moau.moau.accounting.common.service.CategoryCommandService;
import com.moau.moau.accounting.common.service.CategoryQueryService;
import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.global.payload.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Accounting - Common", description = "회계 공통(카테고리) API")
@RestController
@RequestMapping("/api/teams/{teamId}/accounting/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;

    @Operation(summary = "카테고리 생성", description = "(Auth: ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "409", description = "카테고리 이름 중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseDto<CategoryResponseDto>> createCategory(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Valid @RequestBody CategoryRequestDto requestDto
    ) {

        CategoryResponseDto responseDto = categoryCommandService.createCategory(teamId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.data(responseDto));
    }

    @Operation(summary = "카테고리 목록 조회", description = "(Auth: MEMBER)")
    @GetMapping
    public ResponseEntity<ResponseDto<List<CategoryResponseDto>>> getCategories(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "타입 필터 (INCOME | EXPENSE)", example = "EXPENSE")
            @RequestParam(required = false) TransactionType type
    ) {

        List<CategoryResponseDto> responseDto = categoryQueryService.getCategories(teamId, type);
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }

    @Operation(summary = "카테고리 수정", description = "(Auth: ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "카테고리 이름 중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{categoryId}")
    public ResponseEntity<ResponseDto<CategoryResponseDto>> updateCategory(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "카테고리 ID", required = true) @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequestDto requestDto
    ) {
        CategoryResponseDto responseDto = categoryCommandService.updateCategory(teamId, categoryId, requestDto);
        return ResponseEntity.ok(ResponseDto.data(responseDto));
    }
}