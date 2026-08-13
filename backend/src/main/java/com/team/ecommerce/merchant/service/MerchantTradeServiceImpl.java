package com.team.ecommerce.merchant.service;

import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.common.error.ErrorCode;
import com.team.ecommerce.merchant.dto.MerchantDashboardView;
import com.team.ecommerce.merchant.dto.MerchantOrderQuery;
import com.team.ecommerce.merchant.dto.MerchantOrderView;
import com.team.ecommerce.merchant.dto.ShipOrderRequest;
import com.team.ecommerce.trade.order.OrderStatus;
import com.team.ecommerce.trade.order.dto.OrderItemView;
import com.team.ecommerce.trade.order.entity.OrderEntity;
import com.team.ecommerce.trade.order.mapper.OrderItemMapper;
import com.team.ecommerce.trade.order.mapper.OrderMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantTradeServiceImpl implements MerchantTradeService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public MerchantTradeServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public MerchantDashboardView dashboard(Long merchantId) {
        // Query all orders for this merchant (no pagination — in production would use COUNT queries)
        var allOrders = orderMapper.selectByMerchant(merchantId, null, 0, 10000);
        long orderCount = allOrders.size();
        var salesAmount = allOrders.stream()
                .filter(o -> o.payAmount() != null)
                .map(OrderEntity::payAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long pendingShipment = allOrders.stream()
                .filter(o -> o.status() == OrderStatus.PENDING_SHIPMENT.getCode())
                .count();
        return new MerchantDashboardView(orderCount, salesAmount, pendingShipment);
    }

    @Override
    public PageResult<MerchantOrderView> listOrders(Long merchantId, MerchantOrderQuery query) {
        int offset = (query.page() - 1) * query.size();
        var orders = orderMapper.selectByMerchant(merchantId, query.status(), offset, query.size());
        var views = orders.stream().map(this::toMerchantOrderView).toList();
        long total = orderMapper.countByMerchant(merchantId, query.status());
        return new PageResult<>(views, total, query.page(), query.size());
    }

    @Override
    public MerchantOrderView orderDetail(Long merchantId, Long orderId) {
        var order = orderMapper.selectById(orderId);
        if (order == null || !order.merchantId().equals(merchantId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单不存在");
        }
        return toMerchantOrderView(order);
    }

    @Override
    @Transactional
    public MerchantOrderView ship(Long merchantId, Long orderId, ShipOrderRequest request) {
        var order = orderMapper.selectById(orderId);
        if (order == null || !order.merchantId().equals(merchantId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单不存在");
        }
        if (order.status() != OrderStatus.PENDING_SHIPMENT.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "只能对待发货的订单进行发货");
        }
        var now = LocalDateTime.now();
        var updated = new OrderEntity(order.id(), order.orderNo(), order.userId(),
                order.merchantId(), order.totalAmount(), order.payAmount(),
                OrderStatus.SHIPPED.getCode(), order.addressSnapshot(), order.remark(),
                request.logisticsCompany(), request.logisticsNo(), now,
                order.receiveTime(), order.payTime(), order.cancelTime(),
                order.cancelReason(), order.createdAt(), order.updatedAt());
        orderMapper.updateById(updated);
        return toMerchantOrderView(updated);
    }

    private MerchantOrderView toMerchantOrderView(OrderEntity o) {
        var oitems = orderItemMapper.selectByOrderId(o.id());
        var itemViews = oitems.stream().map(oi -> new OrderItemView(oi.id(), oi.productSkuId(),
                oi.productName(), oi.skuName(), oi.productImage(),
                oi.quantity(), oi.unitPrice(), oi.subtotal())).toList();
        return new MerchantOrderView(o.id(), o.orderNo(), o.userId(), o.totalAmount(),
                o.payAmount(), o.status(), o.addressSnapshot(),
                o.logisticsCompany(), o.logisticsNo(), o.createdAt(), itemViews);
    }
}
