package com.team.ecommerce.trade.cart.dto;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequest(@Positive Integer quantity, Boolean selected) {
}
