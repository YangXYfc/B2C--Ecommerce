package com.team.ecommerce.trade.order.service;

import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.FeatureNotImplementedException;
import com.team.ecommerce.trade.order.dto.CancelOrderRequest;
import com.team.ecommerce.trade.order.dto.CreateOrderRequest;
import com.team.ecommerce.trade.order.dto.OrderDetailView;
import com.team.ecommerce.trade.order.dto.OrderQuery;
import com.team.ecommerce.trade.order.dto.OrderSummaryView;
import com.team.ecommerce.trade.order.dto.PaymentView;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceSkeleton implements OrderService {
    public OrderDetailView create(Long userId, CreateOrderRequest request) { throw pending("order.create"); }
    public PageResult<OrderSummaryView> list(Long userId, OrderQuery query) { throw pending("order.list"); }
    public OrderDetailView detail(Long userId, Long orderId) { throw pending("order.detail"); }
    public void cancel(Long userId, Long orderId, CancelOrderRequest request) { throw pending("order.cancel"); }
    public PaymentView pay(Long userId, Long orderId) { throw pending("order.pay"); }
    public OrderDetailView confirmReceipt(Long userId, Long orderId) { throw pending("order.confirmReceipt"); }

    private FeatureNotImplementedException pending(String operation) {
        return new FeatureNotImplementedException(operation);
    }
}
