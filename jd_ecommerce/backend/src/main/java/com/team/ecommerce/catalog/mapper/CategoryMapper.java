package com.team.ecommerce.catalog.mapper;

import com.team.ecommerce.catalog.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品分类 Mapper。
 */
@Mapper
public interface CategoryMapper {

    /** 按状态查询分类，按 sort 升序（越小越靠前）。 */
    List<Category> findByStatus(@Param("status") Integer status);
}
