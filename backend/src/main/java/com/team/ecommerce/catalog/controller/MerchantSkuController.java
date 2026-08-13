package com.team.ecommerce.catalog.controller;

import com.team.ecommerce.catalog.dto.StockRequest;
import com.team.ecommerce.catalog.dto.StockVO;
import com.team.ecommerce.catalog.service.ProductService;
import com.team.ecommerce.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家 SKU 接口（4.9），由 JwtAuthInterceptor 按前缀限制 MERCHANT 角色。
 */
@RestController
@RequestMapping("/api/merchant/skus")
public class MerchantSkuController {

    private final ProductService productService;

    public MerchantSkuController(ProductService productService) {
        this.productService = productService;
    }

    /** 4.9 修改 SKU 库存（绝对值），不允许负数。 */
    @PutMapping("/{id}/stock")
    public Result<StockVO> updateStock(@PathVariable Long id, @Valid @RequestBody StockRequest req) {
        return Result.success(productService.updateStock(id, req.stock()), "库存更新成功");
    }
}
