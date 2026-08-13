package com.team.ecommerce.aftersales.refund;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.team.ecommerce.aftersales.refund.dto.AppealRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.ArbitrateRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.AuditRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.CreateRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.RefundQuery;
import com.team.ecommerce.aftersales.refund.dto.ReturnLogisticsRequest;
import com.team.ecommerce.aftersales.refund.service.RefundService;
import com.team.ecommerce.admin.mapper.AdminLogMapper;
import com.team.ecommerce.common.error.BusinessException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RefundServiceIntegrationTest {

    @Autowired
    private RefundService refundService;

    @Autowired
    private AdminLogMapper adminLogMapper;

    @Test
    void createRefundSetsPending() {
        // Order 2 belongs to user 4 (PENDING_SHIPMENT). Create refund.
        var refund = refundService.create(4L, new CreateRefundRequest(2L, "不想要了",
                "买重复了", new BigDecimal("129.00")));
        assertNotNull(refund.id());
        assertNotNull(refund.refundNo());
        assertEquals(RefundStatus.PENDING.getCode(), refund.status());
    }

    @Test
    void createRefundOnPendingPaymentOrderFails() {
        // Order 4 belongs to user 6 and is PENDING_PAYMENT
        assertThrows(BusinessException.class,
                () -> refundService.create(6L, new CreateRefundRequest(4L, "x", null, new BigDecimal("1"))));
    }

    @Test
    void fullHappyPathRefund() {
        // Create refund on order 2 (user 4, merchant 1)
        var refund = refundService.create(4L, new CreateRefundRequest(2L, "不想要了",
                null, new BigDecimal("129.00")));

        // Merchant 1 approves
        var approved = refundService.merchantAudit(1L, refund.id(),
                new AuditRefundRequest(true, "同意退款"));
        assertEquals(RefundStatus.MERCHANT_APPROVED.getCode(), approved.status());

        // User fills return logistics
        var returning = refundService.submitReturnLogistics(4L, refund.id(),
                new ReturnLogisticsRequest("顺丰速运", "SF9999999999"));
        assertEquals(RefundStatus.RETURNING.getCode(), returning.status());

        // Merchant confirms return -> completed
        var completed = refundService.confirmReturn(1L, refund.id());
        assertEquals(RefundStatus.COMPLETED.getCode(), completed.status());
    }

    @Test
    void merchantRejectThenUserAppealThenAdminArbitrate() {
        var refund = refundService.create(4L, new CreateRefundRequest(2L, "不想要了",
                null, new BigDecimal("129.00")));

        // Merchant rejects
        var rejected = refundService.merchantAudit(1L, refund.id(),
                new AuditRefundRequest(false, "不符合退货条件"));
        assertEquals(RefundStatus.MERCHANT_REJECTED.getCode(), rejected.status());

        // User appeals
        var appealed = refundService.appeal(4L, refund.id(), new AppealRefundRequest("商家无理拒绝"));
        assertEquals(RefundStatus.APPEALED.getCode(), appealed.status());

        // Admin supports user
        int logsBefore = adminLogMapper.selectPage("REFUND_ARBITRATE", 0, 100).size();
        var arbitrated = refundService.arbitrate(1L, refund.id(),
                new ArbitrateRefundRequest(true, "支持用户退款"));
        assertEquals(RefundStatus.ADMIN_APPROVED.getCode(), arbitrated.status());

        // An admin log row was created for this refund
        var logs = adminLogMapper.selectPage("REFUND_ARBITRATE", 0, 100);
        assertEquals(logsBefore + 1, logs.size());
        assertEquals(refund.id(), logs.get(0).targetId()); // newest first
    }

    @Test
    void merchantCannotAuditAnotherMerchantsRefund() {
        var refund = refundService.create(4L, new CreateRefundRequest(2L, "不想要了",
                null, new BigDecimal("129.00")));
        // refund belongs to merchant 1; merchant 2 has no right
        assertThrows(BusinessException.class,
                () -> refundService.merchantAudit(2L, refund.id(), new AuditRefundRequest(true, null)));
    }

    @Test
    void listForAdminOnlyReturnsAppealedRefunds() {
        var page = refundService.listForAdmin(1L, new RefundQuery(null, 1, 10));
        assertNotNull(page.records());
    }
}
