package com.team.ecommerce.trade.cart.dto;

import java.math.BigDecimal;

public record CartItemView(Long id, Long skuId, Long productId, String productName, String skuName, String imageUrl,
        BigDecimal unitPrice, Integer stock, Integer quantity, Boolean selected,
        Long merchantId, String merchantName) {
}
