package com.team.ecommerce.aftersales.refund;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RefundStatusTest {

    @Test
    void mapsEveryDocumentedCode() {
        assertEquals(RefundStatus.PENDING, RefundStatus.fromCode(0));
        assertEquals(RefundStatus.MERCHANT_APPROVED, RefundStatus.fromCode(1));
        assertEquals(RefundStatus.RETURNING, RefundStatus.fromCode(2));
        assertEquals(RefundStatus.COMPLETED, RefundStatus.fromCode(3));
        assertEquals(RefundStatus.MERCHANT_REJECTED, RefundStatus.fromCode(4));
        assertEquals(RefundStatus.APPEALED, RefundStatus.fromCode(5));
        assertEquals(RefundStatus.ADMIN_APPROVED, RefundStatus.fromCode(6));
        assertEquals(RefundStatus.ADMIN_REJECTED, RefundStatus.fromCode(7));
    }

    @Test
    void rejectsUnknownCode() {
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.fromCode(-1));
    }
}
