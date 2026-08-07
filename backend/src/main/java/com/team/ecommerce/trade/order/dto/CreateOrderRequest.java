package com.team.ecommerce.trade.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateOrderRequest(@NotNull Long addressId, @NotEmpty List<@NotNull Long> cartItemIds,
        @Size(max = 255) String remark) {
}
