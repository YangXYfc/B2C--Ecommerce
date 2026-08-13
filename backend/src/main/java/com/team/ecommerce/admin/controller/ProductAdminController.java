package com.team.ecommerce.admin.controller;

import com.team.ecommerce.admin.dto.AdminProductDetailVO;
import com.team.ecommerce.admin.dto.AdminProductPendingVO;
import com.team.ecommerce.admin.dto.ProductAuditRequest;
import com.team.ecommerce.admin.dto.ProductAuditVO;
import com.team.ecommerce.admin.service.AdminProductService;
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
 * 平台商品审核接口（8.1 / 8.2 / 8.3），由 JwtAuthInterceptor 按前缀限制 ADMIN 角色。
 */
@RestController
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    private final AdminProductService adminProductService;

    public ProductAdminController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    /** 8.1 待审核商品列表（纯数组，非分页）。 */
    @GetMapping("/pending")
    public Result<List<AdminProductPendingVO>> listPending() {
        return Result.success(adminProductService.listPending());
    }

    /** 8.2 待审核商品详情（含 SKU）。 */
    @GetMapping("/{id}")
    public Result<AdminProductDetailVO> detail(@PathVariable Long id) {
        return Result.success(adminProductService.detail(id));
    }

    /** 8.3 审核商品（通过/驳回）。 */
    @PutMapping("/{id}/audit")
    public Result<ProductAuditVO> audit(@PathVariable Long id, @Valid @RequestBody ProductAuditRequest req) {
        return Result.success(adminProductService.audit(id, req.approve(), req.remark()), "审核完成");
    }
}
