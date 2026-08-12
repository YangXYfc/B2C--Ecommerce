package com.team.ecommerce.admin.dto;

/**
 * 审核商品响应（8.3）。
 */
public record ProductAuditVO(Long productId, Integer status, String remark) {
}
