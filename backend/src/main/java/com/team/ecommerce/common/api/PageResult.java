package com.team.ecommerce.common.api;

import java.util.List;

public record PageResult<T>(List<T> records, long total, int page, int size) {

    public PageResult {
        records = List.copyOf(records);
    }
}
