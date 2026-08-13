package com.team.ecommerce.common.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PageQuery(
        @Min(1) int page,
        @Min(1) @Max(100) int size) {
}
