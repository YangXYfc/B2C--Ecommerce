package com.team.ecommerce.catalog.controller;

import com.team.ecommerce.catalog.dto.CategoryVO;
import com.team.ecommerce.catalog.service.CategoryService;
import com.team.ecommerce.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类接口（公开）。
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 3.1 分类列表：不带 parentId 返回全部顶级分类树，带 parentId 返回该分类的子分类树。 */
    @GetMapping
    public Result<List<CategoryVO>> list(@RequestParam(required = false) Long parentId) {
        return Result.success(categoryService.list(parentId));
    }
}
