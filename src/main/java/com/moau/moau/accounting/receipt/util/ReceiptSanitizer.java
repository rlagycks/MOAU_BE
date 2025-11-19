package com.moau.moau.accounting.receipt.util;

import java.util.regex.Pattern;

public class ReceiptSanitizer {

    // 1. 전화번호 (010-1234-5678, 02-123-4567 등)
    private static final Pattern PHONE_PATTERN = Pattern.compile("0\\d{1,2}[-.\\s]?\\d{3,4}[-.\\s]?\\d{4}");

    // 2. 주민/외국인 등록번호 (생년월일-뒷자리)
    private static final Pattern RRN_PATTERN = Pattern.compile("\\d{6}[-.\\s]?[1-4]\\d{6}");

    // 3. 카드번호 (12~16자리 숫자) - 마스킹 된 것(****)도 포함해서 넓게 잡음
    private static final Pattern CARD_PATTERN = Pattern.compile("\\d{4}[-.\\s]?\\d{2,4}[-.\\s]?\\d{4}[-.\\s]?\\d{1,4}");

    // 4. 이메일
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");

    public static String sanitize(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return "";
        }

        String sanitized = rawText;
        sanitized = PHONE_PATTERN.matcher(sanitized).replaceAll("[PHONE]");
        sanitized = RRN_PATTERN.matcher(sanitized).replaceAll("[RRN]");
        sanitized = CARD_PATTERN.matcher(sanitized).replaceAll("[CARD]");
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll("[EMAIL]");

        return sanitized;
    }
}