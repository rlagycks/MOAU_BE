// src/main/java/com/moau/moau/team/dto/request/TeamOwnerTransferRequest.java
package com.moau.moau.team.dto.request;

import jakarta.validation.constraints.NotNull;

public record TeamOwnerTransferRequest(Long newOwnerUserId) {}
