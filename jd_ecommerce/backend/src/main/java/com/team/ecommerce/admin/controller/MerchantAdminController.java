package com.team.ecommerce.admin.controller;

import com.team.ecommerce.admin.dto.AdminMerchantDetailVO;
import com.team.ecommerce.admin.dto.AdminMerchantPendingVO;
import com.team.ecommerce.admin.dto.MerchantAuditRequest;
import com.team.ecommerce.admin.dto.MerchantAuditVO;
import com.team.ecommerce.admin.service.AdminMerchantService;
import com.team.ecommerce.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 平台商家审核接口（7.1 / 7.2 / 7.3），由 JwtAuthInterceptor 按前缀限制 ADMIN 角色。
 */
@RestController
@RequestMapping("/api/admin/merchants")
public class MerchantAdminController {

    private final AdminMerchantService adminMerchantService;

    public MerchantAdminController(AdminMerchantService adminMerchantService) {
        this.adminMerchantService = adminMerchantService;
    }

    /** 7.1 待审核商家列表（纯数组，非分页）。 */
    @GetMapping("/pending")
    public Result<List<AdminMerchantPendingVO>> listPending() {
        return Result.success(adminMerchantService.listPending());
    }

    /** 7.2 商家申请详情。 */
    @GetMapping("/{id}")
    public Result<AdminMerchantDetailVO> detail(@PathVariable Long id) {
        return Result.success(adminMerchantService.detail(id));
    }

    /** 7.3 审核商家（通过/驳回）。 */
    @PutMapping("/{id}/audit")
    public Result<MerchantAuditVO> audit(@PathVariable Long id, @Valid @RequestBody MerchantAuditRequest req) {
        return Result.success(adminMerchantService.audit(id, req.approve(), req.remark()), "审核完成");
    }
}
