package com.team.ecommerce.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.ecommerce.admin.entity.AdminLog;
import com.team.ecommerce.admin.mapper.AdminLogMapper;
import com.team.ecommerce.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * 管理员操作日志服务：审核/禁用等操作成功后写入 admin_log。
 * 由调用方（各带 @Transactional 的 admin Service）在事务内调用，日志与业务状态更新同生共死。
 */
@Service
public class AdminLogService {

    private final AdminLogMapper adminLogMapper;
    private final ObjectMapper objectMapper;

    public AdminLogService(AdminLogMapper adminLogMapper, ObjectMapper objectMapper) {
        this.adminLogMapper = adminLogMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 记录一条管理员操作日志。
     *
     * @param action     操作类型，如 MERCHANT_AUDIT/PRODUCT_AUDIT/USER_DISABLE
     * @param targetType 操作对象类型，如 MERCHANT/PRODUCT/USER
     * @param targetId   操作对象 id
     * @param detail     操作详情，序列化为 JSON 字符串（允许值为 null），如 {"action":"approve","remark":"审核通过"}
     */
    public void record(String action, String targetType, Long targetId, Map<String, Object> detail) {
        AdminLog log = new AdminLog();
        log.setAdminId(UserContext.getUserId());
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(toJson(detail));
        log.setIpAddress(currentIp());
        adminLogMapper.insert(log);
    }

    private String toJson(Map<String, Object> detail) {
        if (detail == null || detail.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(detail);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    /** 请求 IP：无请求上下文（如单元测试/定时任务）时为 null。 */
    private String currentIp() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            HttpServletRequest request = attrs.getRequest();
            return request.getRemoteAddr();
        }
        return null;
    }
}
