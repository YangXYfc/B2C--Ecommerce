package com.team.ecommerce.catalog.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 发布/编辑商品请求（4.6 / 4.7）。
 */
public record ProductRequest(
        @NotNull(message = "商品分类不能为空")
        Long categoryId,

        @NotBlank(message = "商品名称不能为空")
        @Size(max = 200, message = "商品名称长度不能超过200")
        String name,

        @Size(max = 255, message = "副标题长度不能超过255")
        String subtitle,

        @NotBlank(message = "商品主图不能为空")
        String mainImage,

        List<String> subImages,

        @Size(max = 2000, message = "商品描述长度不能超过2000")
        String description,

        String detailHtml,

        @NotNull(message = "至少需要一个SKU")
        @Size(min = 1, message = "至少需要一个SKU")
        @Valid
        List<SkuRequest> skus) {
}
