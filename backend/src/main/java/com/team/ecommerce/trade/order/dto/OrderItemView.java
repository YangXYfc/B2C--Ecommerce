package com.team.ecommerce.trade.order.dto;

import java.math.BigDecimal;

public record OrderItemView(Long id, Long skuId, String productName, String skuName, String productImage,
        Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {
}
