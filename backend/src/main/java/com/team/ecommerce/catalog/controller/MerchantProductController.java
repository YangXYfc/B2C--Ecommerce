package com.team.ecommerce.catalog.controller;

import com.team.ecommerce.catalog.dto.MerchantProductDetailVO;
import com.team.ecommerce.catalog.dto.MerchantProductListVO;
import com.team.ecommerce.catalog.dto.ProductIdVO;
import com.team.ecommerce.catalog.dto.ProductRequest;
import com.team.ecommerce.catalog.dto.ProductStatusVO;
import com.team.ecommerce.catalog.service.ProductService;
import com.team.ecommerce.common.PageResult;
import com.team.ecommerce.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家商品接口（4.4–4.8），由 JwtAuthInterceptor 按前缀限制 MERCHANT 角色。
 */
@RestController
@RequestMapping("/api/merchant/products")
public class MerchantProductController {

    private final ProductService productService;

    public MerchantProductController(ProductService productService) {
        this.productService = productService;
    }

    /** 4.4 本店商品列表：可选 status 过滤，含审核状态。 */
    @GetMapping
    public Result<PageResult<MerchantProductListVO>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return Result.success(productService.merchantList(status, page, size));
    }

    /** 4.5 本店商品详情：含审核信息，非本人商品 → 403。 */
    @GetMapping("/{id}")
    public Result<MerchantProductDetailVO> detail(@PathVariable Long id) {
        return Result.success(productService.merchantDetail(id));
    }

    /** 4.6 发布商品：创建后 status=0 待审核，price 取 SKU 最低价。 */
    @PostMapping
    public Result<ProductIdVO> create(@Valid @RequestBody ProductRequest req) {
        return Result.success(productService.create(req), "发布成功，等待审核");
    }

    /** 4.7 编辑商品：整体替换 SKU，重新待审核。 */
    @PutMapping("/{id}")
    public Result<ProductStatusVO> update(@PathVariable Long id, @Valid @RequestBody ProductRequest req) {
        return Result.success(productService.update(id, req), "修改成功，等待审核");
    }

    /** 4.8 下架商品：status → 2。 */
    @PutMapping("/{id}/off-shelf")
    public Result<ProductStatusVO> offShelf(@PathVariable Long id) {
        return Result.success(productService.offShelf(id), "已下架");
    }
}
