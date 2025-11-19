package com.moau.moau.team.controller;

import com.moau.moau.global.exception.ErrorResponse;
import com.moau.moau.team.dto.request.TeamMemberRoleUpdateRequest;
import com.moau.moau.team.dto.request.TeamOwnerTransferRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "👑 Team - Owner", description = "팀장 전용 관리 (역할 변경, 위임) API")
public interface TeamOwnerControllerSwagger {

    @Operation(summary = "멤버 역할 변경", description = "(Auth: OWNER) 멤버를 ADMIN으로 승격하거나 MEMBER로 강등합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "변경 성공"),
            @ApiResponse(responseCode = "403", description = "권한 부족 (OWNER만 가능)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/members/{userId}/role")
    ResponseEntity<Void> changeMemberRole(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @Parameter(description = "대상 멤버 ID", required = true) @PathVariable Long userId,
            @RequestBody @Valid TeamMemberRoleUpdateRequest request
    );

    @Operation(summary = "팀장(Owner) 위임", description = "(Auth: OWNER) 팀장 권한을 다른 멤버에게 넘기고 자신은 멤버가 됩니다.")
    @ApiResponse(responseCode = "204", description = "위임 성공")
    @PatchMapping("/transfer")
    ResponseEntity<Void> transferOwner(
            @Parameter(description = "팀 ID", required = true) @PathVariable Long teamId,
            @RequestBody @Valid TeamOwnerTransferRequest request
    );
}