package com.team.ecommerce.trade.order.service;

import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.trade.order.dto.CancelOrderRequest;
import com.team.ecommerce.trade.order.dto.CreateOrderRequest;
import com.team.ecommerce.trade.order.dto.OrderDetailView;
import com.team.ecommerce.trade.order.dto.OrderQuery;
import com.team.ecommerce.trade.order.dto.OrderSummaryView;
import com.team.ecommerce.trade.order.dto.PaymentView;

public interface OrderService {
    OrderDetailView create(Long userId, CreateOrderRequest request);
    PageResult<OrderSummaryView> list(Long userId, OrderQuery query);
    OrderDetailView detail(Long userId, Long orderId);
    void cancel(Long userId, Long orderId, CancelOrderRequest request);
    PaymentView pay(Long userId, Long orderId);
    OrderDetailView confirmReceipt(Long userId, Long orderId);
}
