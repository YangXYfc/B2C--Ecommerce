package com.team.ecommerce.trade.order;

import java.util.Arrays;

public enum OrderStatus {
    PENDING_PAYMENT(0),
    PENDING_SHIPMENT(1),
    SHIPPED(2),
    RECEIVED(3),
    REVIEWED(4),
    CANCELLED(5);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static OrderStatus fromCode(int code) {
        return Arrays.stream(values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知订单状态: " + code));
    }
}
