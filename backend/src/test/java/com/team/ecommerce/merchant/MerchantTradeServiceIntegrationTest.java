package com.team.ecommerce.merchant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.merchant.dto.MerchantOrderQuery;
import com.team.ecommerce.merchant.dto.ShipOrderRequest;
import com.team.ecommerce.merchant.service.MerchantTradeService;
import com.team.ecommerce.trade.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MerchantTradeServiceIntegrationTest {

    @Autowired
    private MerchantTradeService merchantTradeService;

    @Test
    void dashboardCountsOrdersAndSales() {
        var dashboard = merchantTradeService.dashboard(1L);
        // merchant 1 has orders 1, 2, 4, 6 in data.sql
        assertEquals(4, dashboard.orderCount());
        // paid amounts: order1=4999, order2=129, order6=4299 (order4 unpaid)
        assertEquals(0, dashboard.salesAmount().compareTo(new java.math.BigDecimal("9427")));
        // order 2 is PENDING_SHIPMENT(1)
        assertEquals(1, dashboard.pendingShipmentCount());
    }

    @Test
    void listOrdersForMerchant() {
        var page = merchantTradeService.listOrders(1L, new MerchantOrderQuery(null, 1, 20));
        assertEquals(4, page.records().size());
    }

    @Test
    void shipTransitionsOrderToShipped() {
        // Order 2 belongs to merchant 1, status PENDING_SHIPMENT
        var shipped = merchantTradeService.ship(1L, 2L, new ShipOrderRequest("顺丰速运", "SF12345"));
        assertEquals(OrderStatus.SHIPPED.getCode(), shipped.status());
        assertEquals("SF12345", shipped.logisticsNo());
    }

    @Test
    void shipAnotherMerchantsOrderFails() {
        // Order 3 belongs to merchant 2
        assertThrows(BusinessException.class,
                () -> merchantTradeService.ship(1L, 3L, new ShipOrderRequest("中通", "ZT1")));
    }

    @Test
    void shipNonPendingOrderFails() {
        // Order 1 is RECEIVED(3), cannot ship
        assertThrows(BusinessException.class,
                () -> merchantTradeService.ship(1L, 1L, new ShipOrderRequest("顺丰", "SF1")));
    }

    @Test
    void orderDetailReturnsItems() {
        var detail = merchantTradeService.orderDetail(1L, 1L);
        assertNotNull(detail.items());
        assertEquals(1, detail.items().size());
    }
}
