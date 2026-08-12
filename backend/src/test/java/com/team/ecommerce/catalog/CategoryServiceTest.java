package com.team.ecommerce.catalog;

import com.team.ecommerce.catalog.dto.CategoryVO;
import com.team.ecommerce.catalog.entity.Category;
import com.team.ecommerce.catalog.mapper.CategoryMapper;
import com.team.ecommerce.catalog.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category(long id, long parentId, String name, int sort) {
        Category c = new Category();
        c.setId(id);
        c.setParentId(parentId);
        c.setName(name);
        c.setSort(sort);
        c.setStatus(1);
        return c;
    }

    /** 三级种子数据（模拟 data.sql）。 */
    private List<Category> seed() {
        return List.of(
                category(1, 0, "手机数码", 1),
                category(2, 0, "家用电器", 2),
                category(11, 1, "手机通讯", 1),
                category(12, 1, "电脑办公", 2),
                category(111, 11, "智能手机", 1),
                category(112, 11, "老人机", 2)
        );
    }

    @Test
    void list_noParam_returnsFullTree() {
        when(categoryMapper.findByStatus(1)).thenReturn(seed());

        List<CategoryVO> roots = categoryService.list(null);

        assertEquals(2, roots.size());
        assertEquals("手机数码", roots.get(0).name());
        assertEquals(0L, roots.get(0).parentId());
        assertEquals(2, roots.get(0).children().size());
        assertEquals("手机通讯", roots.get(0).children().get(0).name());
        assertEquals(1L, roots.get(0).children().get(0).parentId());
        assertEquals(2, roots.get(0).children().get(0).children().size());
        // 叶子节点 children 为 null
        assertNull(roots.get(0).children().get(0).children().get(0).children());
    }

    @Test
    void list_withParentId_returnsDirectChildrenWithGrandchildren() {
        when(categoryMapper.findByStatus(1)).thenReturn(seed());

        List<CategoryVO> children = categoryService.list(1L);

        assertEquals(2, children.size());
        assertEquals(11L, children.get(0).id());
        assertEquals(1L, children.get(0).parentId());
        assertEquals(2, children.get(0).children().size());
    }

    @Test
    void list_noData_returnsEmpty() {
        when(categoryMapper.findByStatus(1)).thenReturn(List.of());

        assertEquals(0, categoryService.list(null).size());
    }

    @Test
    void list_parentIdNotFound_returnsEmpty() {
        when(categoryMapper.findByStatus(1)).thenReturn(seed());

        assertEquals(0, categoryService.list(999L).size());
    }
}
