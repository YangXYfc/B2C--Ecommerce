package com.team.ecommerce.catalog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 商品分类节点（分类树 VO）。
 * children 为 null 时序列化省略该键（叶子分类），与契约示例一致；
 * icon 为 null 时仍输出 "icon": null。
 */
public record CategoryVO(
        Long id,
        String name,
        Long parentId,
        Integer sort,
        String icon,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<CategoryVO> children) {
}
