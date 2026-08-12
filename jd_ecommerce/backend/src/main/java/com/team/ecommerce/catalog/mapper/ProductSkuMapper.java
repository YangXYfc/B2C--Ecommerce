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

    /**
     * 删除某商品中「未被订单引用」的 SKU（编辑商品时整体替换的"可删部分"）。
     * 被 {@code order_item} 引用的 SKU 有订单历史，物理删除会触发外键 RESTRICT 报错，因此只删未引用的。
     */
    int deleteUnreferenced(@Param("productId") Long productId);

    /**
     * 停用某商品中「被订单引用」的 SKU（status→0）。保留历史快照供 order_item 对账/售后，
     * 各详情查询只取 status=1，停用后消费者/商家/平台端均不再展示。
     */
    int disableReferenced(@Param("productId") Long productId);

    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);
}
