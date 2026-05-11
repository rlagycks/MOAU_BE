package com.moau.moau.accounting.dues.domain;

import java.time.Instant;

public record DuesStatusChangedEvent(
        Long cycleId,
        Long userId,
        DuesStatus previousStatus,
        DuesStatus newStatus,
        Long amountCents,
        Long changedBy,
        Instant changedAt,
        String cycleName
) {
}
