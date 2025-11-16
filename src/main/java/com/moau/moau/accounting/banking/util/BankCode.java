package com.moau.moau.accounting.banking.util; // (또는 .service.util)

import com.moau.moau.global.exception.error.BankingError;
import com.moau.moau.global.exception.BusinessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BankCode {
    KAKAO_BANK("090", "카카오뱅크"),
    SHINHAN("088", "신한은행"),
    KOOKMIN("004", "KB국민은행"),
    WOORI("020", "우리은행"),
    HANA("081", "하나은행"),
    TOSS_BANK("092", "토스뱅크");
    // (필요한 은행 추가...)

    private final String code;
    private final String name;

    public static BankCode getByCode(String code) {
        return Arrays.stream(BankCode.values())
                .filter(bankCode -> bankCode.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(BankingError.BANK_CODE_NOT_FOUND));
    }
}