package com.team.ecommerce.trade.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartView(List<CartItemView> items, BigDecimal selectedAmount) {
}
