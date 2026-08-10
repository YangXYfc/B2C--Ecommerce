package com.team.ecommerce.catalog.controller;

import com.team.ecommerce.catalog.dto.ProductDetailVO;
import com.team.ecommerce.catalog.dto.ProductListVO;
import com.team.ecommerce.catalog.service.ProductService;
import com.team.ecommerce.common.PageResult;
import com.team.ecommerce.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品公开接口（4.1 / 4.2）。
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** 4.1 商品列表：keyword 模糊匹配 / categoryId 含子分类 / sort 排序 / 分页，仅已上架。 */
    @GetMapping
    public Result<PageResult<ProductListVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return Result.success(productService.list(keyword, categoryId, sort, page, size));
    }

    /** 4.2 商品详情：未上架或不存在返回 404。 */
    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id) {
        return Result.success(productService.detail(id));
    }
}
