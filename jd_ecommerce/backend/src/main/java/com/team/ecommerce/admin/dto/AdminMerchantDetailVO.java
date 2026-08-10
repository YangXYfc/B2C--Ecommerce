package com.team.ecommerce.admin.dto;

/**
 * 商家申请详情（7.2）。
 */
public record AdminMerchantDetailVO(
        Long id,
        Long userId,
        String shopName,
        String shopLogo,
        String description,
        String contactPhone,
        Integer auditStatus,
        String auditRemark,
        Applicant applicant) {

    /** 申请人（商家账号）：含 id/手机号。 */
    public record Applicant(Long id, String username, String nickname, String phone) {
    }
}
