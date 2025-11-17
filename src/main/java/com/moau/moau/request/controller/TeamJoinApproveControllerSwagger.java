// src/main/java/com/moau/moau/request/controller/TeamJoinApproveControllerSwagger.java
package com.moau.moau.request.controller;

import com.moau.moau.request.dto.request.TeamJoinByCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Team Join", description = "팀 초대코드 가입 신청 / 승인 API")
public interface TeamJoinApproveControllerSwagger {

    @Operation(
            summary = "초대코드로 팀 가입 신청",
            description = "멤버가 초대코드를 사용해 팀 가입 신청을 보냅니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "가입 신청 접수 성공"
    )
    ResponseEntity<?> requestJoin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "초대코드 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TeamJoinByCodeRequest.class))
            )
            TeamJoinByCodeRequest req,
            @Parameter(description = "Bearer 토큰")
            String authorization
    );

    @Operation(
            summary = "팀 가입 신청 승인",
            description = "대표(오너)가 특정 가입 신청을 승인합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "가입 신청 승인 성공"
    )
    ResponseEntity<?> approve(
            @Parameter(description = "가입 신청 ID", example = "1")
            Long requestId,
            @Parameter(description = "Bearer 토큰")
            String authorization
    );
}
