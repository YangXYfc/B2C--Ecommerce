package com.team.ecommerce.aftersales.refund;

import java.util.Arrays;

public enum RefundStatus {
    PENDING(0),
    MERCHANT_APPROVED(1),
    RETURNING(2),
    COMPLETED(3),
    MERCHANT_REJECTED(4),
    APPEALED(5),
    ADMIN_APPROVED(6),
    ADMIN_REJECTED(7);

    private final int code;

    RefundStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static RefundStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知退款状态: " + code));
    }
}
