package com.team.ecommerce.admin.dto;

/**
 * 审核商家响应（7.3）。
 */
public record MerchantAuditVO(Long merchantId, Integer auditStatus, String remark) {
}
