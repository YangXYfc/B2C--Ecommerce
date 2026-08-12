package com.team.ecommerce.admin.entity;

import java.time.LocalDateTime;

/**
 * 管理员操作日志实体，对应表 `admin_log`。
 */
public class AdminLog {

    private Long id;
    /** 操作管理员 id（user.id）。 */
    private Long adminId;
    /** 操作类型: MERCHANT_AUDIT/PRODUCT_AUDIT/REFUND_ARBITRATE/USER_DISABLE 等。 */
    private String action;
    /** 操作对象类型: USER/MERCHANT/PRODUCT/ORDER/REFUND。 */
    private String targetType;
    /** 操作对象 id。 */
    private Long targetId;
    /** 操作详情（JSON 字符串）。 */
    private String detail;
    /** 操作 IP。 */
    private String ipAddress;
    /** 创建时间（数据库默认 CURRENT_TIMESTAMP）。 */
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
