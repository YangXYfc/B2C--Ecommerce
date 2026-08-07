package com.team.ecommerce.aftersales.refund.service;

import com.team.ecommerce.aftersales.refund.dto.AppealRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.ArbitrateRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.AuditRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.CreateRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.RefundQuery;
import com.team.ecommerce.aftersales.refund.dto.RefundView;
import com.team.ecommerce.aftersales.refund.dto.ReturnLogisticsRequest;
import com.team.ecommerce.common.api.PageResult;

public interface RefundService {
    RefundView create(Long userId, CreateRefundRequest request);
    PageResult<RefundView> listForUser(Long userId, RefundQuery query);
    RefundView detail(Long userId, Long merchantId, Long adminId, Long refundId);
    RefundView submitReturnLogistics(Long userId, Long refundId, ReturnLogisticsRequest request);
    RefundView appeal(Long userId, Long refundId, AppealRefundRequest request);
    PageResult<RefundView> listForMerchant(Long merchantId, RefundQuery query);
    RefundView merchantAudit(Long merchantId, Long refundId, AuditRefundRequest request);
    RefundView confirmReturn(Long merchantId, Long refundId);
    PageResult<RefundView> listForAdmin(Long adminId, RefundQuery query);
    RefundView arbitrate(Long adminId, Long refundId, ArbitrateRefundRequest request);
}
