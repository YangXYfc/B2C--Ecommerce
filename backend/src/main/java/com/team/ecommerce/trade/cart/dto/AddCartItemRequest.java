package com.team.ecommerce.trade.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartItemRequest(@NotNull Long skuId, @NotNull @Positive Integer quantity) {
}
