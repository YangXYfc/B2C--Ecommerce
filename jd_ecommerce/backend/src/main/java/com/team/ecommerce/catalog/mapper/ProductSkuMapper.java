package com.team.ecommerce.catalog.mapper;

import com.team.ecommerce.catalog.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品 SKU Mapper。
 */
@Mapper
public interface ProductSkuMapper {

    /** 某商品启用的 SKU 列表。 */
    List<ProductSku> findByProductId(@Param("productId") Long productId);

    ProductSku findById(@Param("id") Long id);

    /** 新增 SKU，成功后回填自增主键到 {@code sku.id}。 */
    int insert(ProductSku sku);

    /** 删除某商品的全部 SKU（编辑时整体替换）。 */
    int deleteByProductId(@Param("productId") Long productId);

    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);
}
