package com.moau.moau.team.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeamCreateRequest(
        @NotBlank String name,
        String description
) {}
