package com.team.ecommerce.trade.inventory;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Minimal read/write mapper for product_sku table, used only by {@link InventoryGatewayImpl}.
 * D role owns the full SKU CRUD; E owns only stock check, deduct, and restore.
 */
@Mapper
public interface ProductSkuMapper {

    /** Returns current stock of a SKU, or null if SKU does not exist. */
    Integer selectStockForUpdate(@Param("skuId") Long skuId);

    /** Atomically decrements stock by {@code quantity}. Returns 1 on success. */
    int deductStock(@Param("skuId") Long skuId, @Param("quantity") int quantity);

    /** Atomically increments stock by {@code quantity}. Returns 1 on success. */
    int restoreStock(@Param("skuId") Long skuId, @Param("quantity") int quantity);
}
