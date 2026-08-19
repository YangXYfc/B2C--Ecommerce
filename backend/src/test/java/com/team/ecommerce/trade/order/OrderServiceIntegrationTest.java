package com.team.ecommerce.trade.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.trade.inventory.TradeProductSkuMapper;
import com.team.ecommerce.trade.order.dto.CancelOrderRequest;
import com.team.ecommerce.trade.order.dto.CreateOrderRequest;
import com.team.ecommerce.trade.order.dto.OrderQuery;
import com.team.ecommerce.trade.order.service.OrderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeProductSkuMapper productSkuMapper;

    private static final Long USER_ID = 4L;

    @Test
    void createOrderSplitsByMerchantAndDeductsStock() {
        // user 4 selected cart items 1 (merchant1 sku1) and 2 (merchant2 sku11)
        var order = orderService.create(USER_ID, new CreateOrderRequest(1L, List.of(1L, 2L), null));

        assertNotNull(order.id());
        assertNotNull(order.orderNo());
        assertNotNull(order.items().get(0).productId());
        // First order returned is for the first merchant in grouping order
        assertEquals(OrderStatus.PENDING_PAYMENT.getCode(), order.status());
        // SKU1 stock was 500; after deduct 1 -> 499
        assertEquals(499, productSkuMapper.selectStockForUpdate(1L));
    }

    @Test
    void createOrderWithInvalidAddressFails() {
        assertThrows(BusinessException.class,
                () -> orderService.create(USER_ID, new CreateOrderRequest(999L, List.of(1L), null)));
    }

    @Test
    void payTransitionsOrderToPendingShipment() {
        var order = orderService.create(USER_ID, new CreateOrderRequest(1L, List.of(1L), null));
        var payment = orderService.pay(USER_ID, order.id());
        assertNotNull(payment.paymentNo());
        assertEquals("SIMULATED", payment.payMethod());

        var detail = orderService.detail(USER_ID, order.id());
        assertEquals(OrderStatus.PENDING_SHIPMENT.getCode(), detail.status());
        assertNotNull(detail.payAmount());
    }

    @Test
    void cancelRestoresStock() {
        var order = orderService.create(USER_ID, new CreateOrderRequest(1L, List.of(1L), null));
        int before = productSkuMapper.selectStockForUpdate(1L); // 499 after deduct
        orderService.cancel(USER_ID, order.id(), new CancelOrderRequest("不想买了"));
        int after = productSkuMapper.selectStockForUpdate(1L);
        assertEquals(before + 1, after); // restored
        assertEquals(OrderStatus.CANCELLED.getCode(), orderService.detail(USER_ID, order.id()).status());
    }

    @Test
    void payAlreadyPaidOrderFails() {
        var order = orderService.create(USER_ID, new CreateOrderRequest(1L, List.of(1L), null));
        orderService.pay(USER_ID, order.id());
        assertThrows(BusinessException.class, () -> orderService.pay(USER_ID, order.id()));
    }

    @Test
    void confirmReceiptOnlyForShippedOrder() {
        var order = orderService.create(USER_ID, new CreateOrderRequest(1L, List.of(1L), null));
        // Order is PENDING_PAYMENT; confirm should fail
        assertThrows(BusinessException.class, () -> orderService.confirmReceipt(USER_ID, order.id()));
    }

    @Test
    void listOrdersByUser() {
        var page = orderService.list(USER_ID, new OrderQuery(null, 1, 10));
        assertNotNull(page.records());
        assertEquals(1, page.page());
        // data.sql: user 4 has orders 1, 2, 5
        assertEquals(3, page.total());
    }

    @Test
    void detailBelongingToAnotherUserFails() {
        assertThrows(BusinessException.class, () -> orderService.detail(USER_ID, 3L)); // order 3 belongs to user 5
    }
}
