package com.team.ecommerce.aftersales.refund.service;

import com.team.ecommerce.aftersales.refund.RefundStatus;
import com.team.ecommerce.aftersales.refund.dto.*;
import com.team.ecommerce.aftersales.refund.entity.RefundEntity;
import com.team.ecommerce.aftersales.refund.mapper.RefundMapper;
import com.team.ecommerce.admin.entity.AdminLogEntity;
import com.team.ecommerce.admin.mapper.AdminLogMapper;
import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.common.error.ErrorCode;
import com.team.ecommerce.trade.order.OrderStatus;
import com.team.ecommerce.trade.order.mapper.OrderMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundServiceImpl implements RefundService {

    private final RefundMapper refundMapper;
    private final OrderMapper orderMapper;
    private final AdminLogMapper adminLogMapper;

    public RefundServiceImpl(RefundMapper refundMapper, OrderMapper orderMapper,
            AdminLogMapper adminLogMapper) {
        this.refundMapper = refundMapper;
        this.orderMapper = orderMapper;
        this.adminLogMapper = adminLogMapper;
    }

    @Override
    @Transactional
    public RefundView create(Long userId, CreateRefundRequest request) {
        var order = orderMapper.selectById(request.orderId());
        if (order == null || !order.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单不存在");
        }
        // Can only refund paid/shipped/received orders
        int status = order.status();
        if (status != OrderStatus.PENDING_SHIPMENT.getCode()
                && status != OrderStatus.SHIPPED.getCode()
                && status != OrderStatus.RECEIVED.getCode()
                && status != OrderStatus.REVIEWED.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单状态不支持退款");
        }
        var refundNo = "RFD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%05d", new Random().nextInt(100000));
        var entity = new RefundEntity(null, refundNo, request.orderId(), userId, order.merchantId(),
                request.reason(), request.description(), request.amount(),
                RefundStatus.PENDING.getCode(),
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                48, null, null);
        refundMapper.insert(entity);
        var inserted = new RefundEntity(refundMapper.lastInsertId(), entity.refundNo(),
                entity.orderId(), entity.userId(), entity.merchantId(), entity.reason(),
                entity.description(), entity.amount(), entity.status(),
                entity.merchantAuditTime(), entity.merchantRemark(),
                entity.returnLogisticsCompany(), entity.returnLogisticsNo(),
                entity.returnShipTime(), entity.merchantConfirmTime(), entity.appealTime(),
                entity.appealReason(), entity.adminId(), entity.adminHandleTime(),
                entity.adminRemark(), entity.completedTime(), entity.timeoutHours(),
                entity.createdAt(), entity.updatedAt());
        return toView(inserted);
    }

    @Override
    public PageResult<RefundView> listForUser(Long userId, RefundQuery query) {
        int offset = (query.page() - 1) * query.size();
        var refunds = refundMapper.selectByUser(userId, query.status(), offset, query.size());
        var views = refunds.stream().map(RefundServiceImpl::toView).toList();
        long total = refundMapper.countByUser(userId, query.status());
        return new PageResult<>(views, total, query.page(), query.size());
    }

    @Override
    public RefundView detail(Long userId, Long merchantId, Long adminId, Long refundId) {
        var refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "退款单不存在");
        }
        // At least one identity must match
        boolean ok = (userId != null && refund.userId().equals(userId))
                || (merchantId != null && refund.merchantId().equals(merchantId))
                || (adminId != null);
        if (!ok) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "无权查看该退款单");
        }
        return toView(refund);
    }

    @Override
    @Transactional
    public RefundView submitReturnLogistics(Long userId, Long refundId,
            ReturnLogisticsRequest request) {
        var refund = refundMapper.selectById(refundId);
        if (refund == null || !refund.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "退款单不存在");
        }
        if (refund.status() != RefundStatus.MERCHANT_APPROVED.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "当前状态不允许填写物流");
        }
        var now = LocalDateTime.now();
        var updated = new RefundEntity(refund.id(), refund.refundNo(), refund.orderId(),
                refund.userId(), refund.merchantId(), refund.reason(), refund.description(),
                refund.amount(), RefundStatus.RETURNING.getCode(),
                refund.merchantAuditTime(), refund.merchantRemark(),
                request.logisticsCompany(), request.logisticsNo(), now,
                refund.merchantConfirmTime(), refund.appealTime(), refund.appealReason(),
                refund.adminId(), refund.adminHandleTime(), refund.adminRemark(),
                refund.completedTime(), refund.timeoutHours(),
                refund.createdAt(), refund.updatedAt());
        refundMapper.updateById(updated);
        return toView(updated);
    }

    @Override
    @Transactional
    public RefundView appeal(Long userId, Long refundId, AppealRefundRequest request) {
        var refund = refundMapper.selectById(refundId);
        if (refund == null || !refund.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "退款单不存在");
        }
        if (refund.status() != RefundStatus.MERCHANT_REJECTED.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "只能对商家拒绝的退款进行申诉");
        }
        var now = LocalDateTime.now();
        var updated = new RefundEntity(refund.id(), refund.refundNo(), refund.orderId(),
                refund.userId(), refund.merchantId(), refund.reason(), refund.description(),
                refund.amount(), RefundStatus.APPEALED.getCode(),
                refund.merchantAuditTime(), refund.merchantRemark(),
                refund.returnLogisticsCompany(), refund.returnLogisticsNo(),
                refund.returnShipTime(), refund.merchantConfirmTime(),
                now, request.reason(),
                refund.adminId(), refund.adminHandleTime(), refund.adminRemark(),
                refund.completedTime(), refund.timeoutHours(),
                refund.createdAt(), refund.updatedAt());
        refundMapper.updateById(updated);
        return toView(updated);
    }

    @Override
    public PageResult<RefundView> listForMerchant(Long merchantId, RefundQuery query) {
        int offset = (query.page() - 1) * query.size();
        var refunds = refundMapper.selectByMerchant(merchantId, query.status(), offset, query.size());
        var views = refunds.stream().map(RefundServiceImpl::toView).toList();
        long total = refundMapper.countByMerchant(merchantId, query.status());
        return new PageResult<>(views, total, query.page(), query.size());
    }

    @Override
    @Transactional
    public RefundView merchantAudit(Long merchantId, Long refundId,
            AuditRefundRequest request) {
        var refund = refundMapper.selectById(refundId);
        if (refund == null || !refund.merchantId().equals(merchantId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "退款单不存在");
        }
        if (refund.status() != RefundStatus.PENDING.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "退款单状态不正确");
        }
        var now = LocalDateTime.now();
        var newStatus = request.approved()
                ? RefundStatus.MERCHANT_APPROVED.getCode()
                : RefundStatus.MERCHANT_REJECTED.getCode();
        var updated = new RefundEntity(refund.id(), refund.refundNo(), refund.orderId(),
                refund.userId(), refund.merchantId(), refund.reason(), refund.description(),
                refund.amount(), newStatus,
                now, request.remark(),
                refund.returnLogisticsCompany(), refund.returnLogisticsNo(),
                refund.returnShipTime(), refund.merchantConfirmTime(),
                refund.appealTime(), refund.appealReason(),
                refund.adminId(), refund.adminHandleTime(), refund.adminRemark(),
                refund.completedTime(), refund.timeoutHours(),
                refund.createdAt(), refund.updatedAt());
        refundMapper.updateById(updated);
        return toView(updated);
    }

    @Override
    @Transactional
    public RefundView confirmReturn(Long merchantId, Long refundId) {
        var refund = refundMapper.selectById(refundId);
        if (refund == null || !refund.merchantId().equals(merchantId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "退款单不存在");
        }
        if (refund.status() != RefundStatus.RETURNING.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "退款单状态不正确，需要用户先寄回商品");
        }
        var now = LocalDateTime.now();
        var updated = new RefundEntity(refund.id(), refund.refundNo(), refund.orderId(),
                refund.userId(), refund.merchantId(), refund.reason(), refund.description(),
                refund.amount(), RefundStatus.COMPLETED.getCode(),
                refund.merchantAuditTime(), refund.merchantRemark(),
                refund.returnLogisticsCompany(), refund.returnLogisticsNo(),
                refund.returnShipTime(), now,
                refund.appealTime(), refund.appealReason(),
                refund.adminId(), refund.adminHandleTime(), refund.adminRemark(),
                now, refund.timeoutHours(),
                refund.createdAt(), refund.updatedAt());
        refundMapper.updateById(updated);
        return toView(updated);
    }

    @Override
    public PageResult<RefundView> listForAdmin(Long adminId, RefundQuery query) {
        int offset = (query.page() - 1) * query.size();
        var refunds = refundMapper.selectForArbitration(offset, query.size());
        var views = refunds.stream().map(RefundServiceImpl::toView).toList();
        long total = refundMapper.countForArbitration();
        return new PageResult<>(views, total, query.page(), query.size());
    }

    @Override
    @Transactional
    public RefundView arbitrate(Long adminId, Long refundId,
            ArbitrateRefundRequest request) {
        var refund = refundMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "退款单不存在");
        }
        if (refund.status() != RefundStatus.APPEALED.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "退款单状态不正确，只能仲裁申诉中的退款");
        }
        var now = LocalDateTime.now();
        var newStatus = request.supportUser()
                ? RefundStatus.ADMIN_APPROVED.getCode()
                : RefundStatus.ADMIN_REJECTED.getCode();
        var completedTime = request.supportUser() ? now : null;
        var updated = new RefundEntity(refund.id(), refund.refundNo(), refund.orderId(),
                refund.userId(), refund.merchantId(), refund.reason(), refund.description(),
                refund.amount(), newStatus,
                refund.merchantAuditTime(), refund.merchantRemark(),
                refund.returnLogisticsCompany(), refund.returnLogisticsNo(),
                refund.returnShipTime(), refund.merchantConfirmTime(),
                refund.appealTime(), refund.appealReason(),
                adminId, now, request.remark(),
                completedTime, refund.timeoutHours(),
                refund.createdAt(), refund.updatedAt());
        refundMapper.updateById(updated);

        // Log admin operation
        var log = new AdminLogEntity(null, adminId, "REFUND_ARBITRATE", "REFUND", refundId,
                "{\"action\":\"" + (request.supportUser() ? "approve" : "reject")
                        + "\",\"remark\":\"" + (request.remark() != null ? request.remark() : "")
                        + "\"}", "127.0.0.1", null);
        adminLogMapper.insert(log);

        return toView(updated);
    }

    private static RefundView toView(RefundEntity e) {
        return new RefundView(e.id(), e.refundNo(), e.orderId(), e.userId(), e.merchantId(),
                e.reason(), e.description(), e.amount(), e.status(),
                e.returnLogisticsCompany(), e.returnLogisticsNo(), e.appealReason(),
                e.merchantRemark(), e.adminRemark(), e.createdAt(), e.updatedAt());
    }
}
