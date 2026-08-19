package com.team.ecommerce.trade.order.service;

import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.common.error.ErrorCode;
import com.team.ecommerce.trade.cart.mapper.CartMapper;
import com.team.ecommerce.trade.inventory.InventoryGateway;
import com.team.ecommerce.trade.inventory.InventoryItem;
import com.team.ecommerce.trade.order.OrderStatus;
import com.team.ecommerce.trade.order.dto.*;
import com.team.ecommerce.trade.order.entity.OrderEntity;
import com.team.ecommerce.trade.order.entity.OrderItemEntity;
import com.team.ecommerce.trade.order.mapper.AddressReadMapper;
import com.team.ecommerce.trade.order.mapper.OrderItemMapper;
import com.team.ecommerce.trade.order.mapper.OrderMapper;
import com.team.ecommerce.trade.order.mapper.ProductReadMapper;
import com.team.ecommerce.trade.payment.entity.PaymentEntity;
import com.team.ecommerce.trade.payment.mapper.PaymentMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final PaymentMapper paymentMapper;
    private final ProductReadMapper productReadMapper;
    private final AddressReadMapper addressReadMapper;
    private final InventoryGateway inventoryGateway;

    public OrderServiceImpl(CartMapper cartMapper, OrderMapper orderMapper,
            OrderItemMapper orderItemMapper, PaymentMapper paymentMapper,
            ProductReadMapper productReadMapper, AddressReadMapper addressReadMapper,
            InventoryGateway inventoryGateway) {
        this.cartMapper = cartMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.paymentMapper = paymentMapper;
        this.productReadMapper = productReadMapper;
        this.addressReadMapper = addressReadMapper;
        this.inventoryGateway = inventoryGateway;
    }

    @Override
    @Transactional
    public OrderDetailView create(Long userId, CreateOrderRequest request) {
        // 1. Read address
        var addr = addressReadMapper.selectById(request.addressId());
        if (addr == null) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "收货地址不存在");
        }
        var addrJson = String.format(
                "{\"name\":\"%s\",\"phone\":\"%s\",\"province\":\"%s\",\"city\":\"%s\",\"district\":\"%s\",\"detail\":\"%s\"}",
                addr.name(), addr.phone(), addr.province(), addr.city(),
                addr.district() != null ? addr.district() : "", addr.detail());

        // 2. Read cart items with SKU/product info
        record CartSku(Long cartId, ProductReadMapper.SkuInfo sku, int qty) {}
        var items = new ArrayList<CartSku>();
        for (var cartId : request.cartItemIds()) {
            var cart = cartMapper.selectById(cartId);
            if (cart == null || !cart.userId().equals(userId)) {
                throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "购物车项不存在: " + cartId);
            }
            var sku = productReadMapper.selectSkuInfo(cart.productSkuId());
            if (sku == null) {
                throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "SKU不存在: " + cart.productSkuId());
            }
            items.add(new CartSku(cartId, sku, cart.quantity()));
        }

        // 3. Check inventory
        var invItems = items.stream()
                .map(i -> new InventoryItem(i.sku.skuId(), i.qty()))
                .toList();
        inventoryGateway.check(invItems);
        inventoryGateway.deduct(invItems);

        // 4. Group by merchant, create orders
        var byMerchant = items.stream()
                .collect(Collectors.groupingBy(i -> i.sku.merchantId(), LinkedHashMap::new, Collectors.toList()));

        OrderDetailView firstOrder = null;
        for (var entry : byMerchant.entrySet()) {
            var merchantId = entry.getKey();
            var merchantItems = entry.getValue();

            var totalAmount = merchantItems.stream()
                    .map(i -> i.sku.price().multiply(BigDecimal.valueOf(i.qty())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            var orderNo = generateOrderNo();
            var order = new OrderEntity(null, orderNo, userId, merchantId,
                    totalAmount, null, OrderStatus.PENDING_PAYMENT.getCode(),
                    addrJson, request.remark(), null, null,
                    null, null, null, null, null, null, null);
            orderMapper.insert(order);
            // Records are immutable so re-select the row to get the DB-generated id,
            // timestamps and any defaulted columns.
            var insertedOrder = orderMapper.selectById(orderMapper.lastInsertId());

            // Insert order items
            var orderItemViews = new ArrayList<OrderItemView>();
            for (var ci : merchantItems) {
                var subtotal = ci.sku.price().multiply(BigDecimal.valueOf(ci.qty()));
                var oi = new OrderItemEntity(null, insertedOrder.id(), ci.sku.skuId(),
                        ci.sku.productName(), ci.sku.skuName(), ci.sku.productImage(),
                        ci.qty(), ci.sku.price(), subtotal, null);
                orderItemMapper.insert(oi);
                var oiWithId = new OrderItemEntity(orderItemMapper.lastInsertId(), oi.orderId(),
                        oi.productSkuId(), oi.productName(), oi.skuName(), oi.productImage(),
                        oi.quantity(), oi.unitPrice(), oi.subtotal(), oi.createdAt());
                orderItemViews.add(new OrderItemView(oiWithId.id(), oiWithId.productSkuId(), ci.sku.productId(),
                        oiWithId.productName(), oiWithId.skuName(), oiWithId.productImage(),
                        oiWithId.quantity(), oiWithId.unitPrice(), oiWithId.subtotal()));
            }

            // Delete cart items
            for (var ci : merchantItems) {
                cartMapper.deleteById(ci.cartId(), userId);
            }

            var detail = new OrderDetailView(insertedOrder.id(), insertedOrder.orderNo(),
                    insertedOrder.merchantId(),
                    insertedOrder.totalAmount(), insertedOrder.payAmount(), insertedOrder.status(),
                    insertedOrder.addressSnapshot(), insertedOrder.remark(),
                    insertedOrder.logisticsCompany(), insertedOrder.logisticsNo(),
                    insertedOrder.createdAt(), orderItemViews);
            if (firstOrder == null) {
                firstOrder = detail;
            }
        }
        return firstOrder;
    }

    @Override
    public PageResult<OrderSummaryView> list(Long userId, OrderQuery query) {
        int offset = (query.page() - 1) * query.size();
        var orders = orderMapper.selectByUser(userId, query.status(), offset, query.size());
        var views = orders.stream().map(o -> {
            var oitems = orderItemMapper.selectByOrderId(o.id());
            var itemViews = oitems.stream().map(oi -> new OrderItemView(oi.id(), oi.productSkuId(), productIdOf(oi.productSkuId()),
                    oi.productName(), oi.skuName(), oi.productImage(),
                    oi.quantity(), oi.unitPrice(), oi.subtotal())).toList();
            return new OrderSummaryView(o.id(), o.orderNo(), o.merchantId(),
                    o.totalAmount(), o.payAmount(), o.status(), o.createdAt(), itemViews);
        }).toList();
        long total = orderMapper.countByUser(userId, query.status());
        return new PageResult<>(views, total, query.page(), query.size());
    }

    @Override
    public OrderDetailView detail(Long userId, Long orderId) {
        var order = orderMapper.selectById(orderId);
        if (order == null || !order.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单不存在");
        }
        var oitems = orderItemMapper.selectByOrderId(orderId);
        var itemViews = oitems.stream().map(oi -> new OrderItemView(oi.id(), oi.productSkuId(), productIdOf(oi.productSkuId()),
                oi.productName(), oi.skuName(), oi.productImage(),
                oi.quantity(), oi.unitPrice(), oi.subtotal())).toList();
        return new OrderDetailView(order.id(), order.orderNo(), order.merchantId(),
                order.totalAmount(), order.payAmount(), order.status(),
                order.addressSnapshot(), order.remark(),
                order.logisticsCompany(), order.logisticsNo(), order.createdAt(), itemViews);
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long orderId, CancelOrderRequest request) {
        var order = orderMapper.selectById(orderId);
        if (order == null || !order.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单不存在");
        }
        if (order.status() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "只能取消待支付订单");
        }
        // Restore inventory
        var oitems = orderItemMapper.selectByOrderId(orderId);
        var invItems = oitems.stream()
                .map(oi -> new InventoryItem(oi.productSkuId(), oi.quantity()))
                .toList();
        inventoryGateway.restore(invItems);

        var updated = new OrderEntity(order.id(), order.orderNo(), order.userId(),
                order.merchantId(), order.totalAmount(), order.payAmount(),
                OrderStatus.CANCELLED.getCode(), order.addressSnapshot(), order.remark(),
                order.logisticsCompany(), order.logisticsNo(), order.shipTime(),
                order.receiveTime(), order.payTime(), LocalDateTime.now(),
                request.reason(), order.createdAt(), order.updatedAt());
        orderMapper.updateById(updated);
    }

    @Override
    @Transactional
    public PaymentView pay(Long userId, Long orderId) {
        var order = orderMapper.selectById(orderId);
        if (order == null || !order.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单不存在");
        }
        if (order.status() != OrderStatus.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单状态不正确，无法支付");
        }
        var now = LocalDateTime.now();
        var paymentNo = "PAY" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
        var payment = new PaymentEntity(null, paymentNo, orderId, userId,
                order.totalAmount(), "SIMULATED", 1, now, null, null);
        paymentMapper.insert(payment);
        var insertedPayment = new PaymentEntity(paymentMapper.lastInsertId(), payment.paymentNo(),
                payment.orderId(), payment.userId(), payment.amount(), payment.payMethod(),
                payment.status(), payment.payTime(), payment.createdAt(), payment.updatedAt());

        var updated = new OrderEntity(order.id(), order.orderNo(), order.userId(),
                order.merchantId(), order.totalAmount(), order.totalAmount(),
                OrderStatus.PENDING_SHIPMENT.getCode(), order.addressSnapshot(),
                order.remark(), order.logisticsCompany(), order.logisticsNo(),
                order.shipTime(), order.receiveTime(), now,
                order.cancelTime(), order.cancelReason(),
                order.createdAt(), order.updatedAt());
        orderMapper.updateById(updated);

        return new PaymentView(insertedPayment.id(), insertedPayment.paymentNo(),
                insertedPayment.orderId(),
                insertedPayment.amount(), insertedPayment.payMethod(), insertedPayment.status(),
                insertedPayment.payTime());
    }

    @Override
    @Transactional
    public OrderDetailView confirmReceipt(Long userId, Long orderId) {
        var order = orderMapper.selectById(orderId);
        if (order == null || !order.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单不存在");
        }
        if (order.status() != OrderStatus.SHIPPED.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "只能确认已发货的订单");
        }
        var now = LocalDateTime.now();
        var updated = new OrderEntity(order.id(), order.orderNo(), order.userId(),
                order.merchantId(), order.totalAmount(), order.payAmount(),
                OrderStatus.RECEIVED.getCode(), order.addressSnapshot(), order.remark(),
                order.logisticsCompany(), order.logisticsNo(), order.shipTime(),
                now, order.payTime(), order.cancelTime(), order.cancelReason(),
                order.createdAt(), order.updatedAt());
        orderMapper.updateById(updated);

        var oitems = orderItemMapper.selectByOrderId(orderId);
        var itemViews = oitems.stream().map(oi -> new OrderItemView(oi.id(), oi.productSkuId(), productIdOf(oi.productSkuId()),
                oi.productName(), oi.skuName(), oi.productImage(),
                oi.quantity(), oi.unitPrice(), oi.subtotal())).toList();
        return new OrderDetailView(updated.id(), updated.orderNo(), updated.merchantId(),
                updated.totalAmount(), updated.payAmount(), updated.status(),
                updated.addressSnapshot(), updated.remark(),
                updated.logisticsCompany(), updated.logisticsNo(), updated.createdAt(), itemViews);
    }

    private static String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%05d", new Random().nextInt(100000));
    }

    private Long productIdOf(Long skuId) {
        var sku = productReadMapper.selectSkuInfo(skuId);
        return sku != null ? sku.productId() : null;
    }
}
