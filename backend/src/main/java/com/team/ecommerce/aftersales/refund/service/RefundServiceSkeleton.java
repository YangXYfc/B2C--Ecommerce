package com.team.ecommerce.aftersales.refund.service;

import com.team.ecommerce.aftersales.refund.dto.AppealRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.ArbitrateRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.AuditRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.CreateRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.RefundQuery;
import com.team.ecommerce.aftersales.refund.dto.RefundView;
import com.team.ecommerce.aftersales.refund.dto.ReturnLogisticsRequest;
import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.FeatureNotImplementedException;
import org.springframework.stereotype.Service;

@Service
public class RefundServiceSkeleton implements RefundService {
    public RefundView create(Long userId, CreateRefundRequest request) { throw pending("refund.create"); }
    public PageResult<RefundView> listForUser(Long userId, RefundQuery query) { throw pending("refund.listForUser"); }
    public RefundView detail(Long userId, Long merchantId, Long adminId, Long refundId) { throw pending("refund.detail"); }
    public RefundView submitReturnLogistics(Long userId, Long refundId, ReturnLogisticsRequest request) { throw pending("refund.returnLogistics"); }
    public RefundView appeal(Long userId, Long refundId, AppealRefundRequest request) { throw pending("refund.appeal"); }
    public PageResult<RefundView> listForMerchant(Long merchantId, RefundQuery query) { throw pending("refund.listForMerchant"); }
    public RefundView merchantAudit(Long merchantId, Long refundId, AuditRefundRequest request) { throw pending("refund.merchantAudit"); }
    public RefundView confirmReturn(Long merchantId, Long refundId) { throw pending("refund.confirmReturn"); }
    public PageResult<RefundView> listForAdmin(Long adminId, RefundQuery query) { throw pending("refund.listForAdmin"); }
    public RefundView arbitrate(Long adminId, Long refundId, ArbitrateRefundRequest request) { throw pending("refund.arbitrate"); }
    private FeatureNotImplementedException pending(String operation) { return new FeatureNotImplementedException(operation); }
}
