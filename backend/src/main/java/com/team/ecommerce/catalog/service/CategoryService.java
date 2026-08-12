package com.team.ecommerce.catalog.service;

import com.team.ecommerce.catalog.dto.CategoryVO;
import com.team.ecommerce.catalog.entity.Category;
import com.team.ecommerce.catalog.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类服务。
 */
@Service
public class CategoryService {

    /** 状态：1-显示。 */
    private static final int STATUS_SHOW = 1;

    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 3.1 分类列表：不带 parentId 返回全部顶级分类树（parentId=0 为根），
     * 带 parentId 返回该分类的子分类树。
     */
    public List<CategoryVO> list(Long parentId) {
        Map<Long, List<Category>> byParent = categoryMapper.findByStatus(STATUS_SHOW).stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        return buildTree(parentId == null ? 0L : parentId, byParent);
    }

    /** 递归构建以 parentId 为父节点的分类树；叶子节点 children 为 null。 */
    private List<CategoryVO> buildTree(Long parentId, Map<Long, List<Category>> byParent) {
        return byParent.getOrDefault(parentId, List.of()).stream()
                .map(c -> {
                    List<CategoryVO> children = buildTree(c.getId(), byParent);
                    return new CategoryVO(c.getId(), c.getName(), c.getParentId(), c.getSort(),
                            c.getIcon(), children.isEmpty() ? null : children);
                })
                .toList();
    }
}
