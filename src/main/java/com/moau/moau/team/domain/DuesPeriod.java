package com.moau.moau.team.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DuesPeriod {
    NONE("없음", 0),
    MONTHLY("매월", 1),
    QUARTERLY("분기(3개월)", 3),
    HALF_YEARLY("반기(6개월)", 6),
    YEARLY("매년(12개월)", 12);

    private final String description;
    private final int months;
}