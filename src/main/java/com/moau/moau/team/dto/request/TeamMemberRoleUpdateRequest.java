// src/main/java/com/moau/moau/team/dto/request/TeamMemberRoleUpdateRequest.java
package com.moau.moau.team.dto.request;

import com.moau.moau.team.domain.TeamMemberRole;
import jakarta.validation.constraints.NotNull;

public record TeamMemberRoleUpdateRequest(
        @NotNull TeamMemberRole role
) {
}
