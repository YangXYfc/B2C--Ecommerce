package com.team.ecommerce.trade.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class OrderStatusTest {

    @Test
    void mapsEveryDocumentedCode() {
        assertEquals(OrderStatus.PENDING_PAYMENT, OrderStatus.fromCode(0));
        assertEquals(OrderStatus.PENDING_SHIPMENT, OrderStatus.fromCode(1));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.fromCode(2));
        assertEquals(OrderStatus.RECEIVED, OrderStatus.fromCode(3));
        assertEquals(OrderStatus.REVIEWED, OrderStatus.fromCode(4));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.fromCode(5));
    }

    @Test
    void rejectsUnknownCode() {
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.fromCode(-1));
    }
}
