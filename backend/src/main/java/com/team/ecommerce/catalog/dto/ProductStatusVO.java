package com.team.ecommerce.catalog.dto;

/**
 * 编辑/下架商品响应（4.7 / 4.8）。
 */
public record ProductStatusVO(Long productId, Integer status) {
}
