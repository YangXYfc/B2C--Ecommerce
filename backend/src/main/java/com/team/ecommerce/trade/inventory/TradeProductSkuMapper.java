package com.team.ecommerce.trade.inventory;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** E-role stock mapper; the catalog module owns the full SKU mapper. */
@Mapper
public interface TradeProductSkuMapper {
    Integer selectStockForUpdate(@Param("skuId") Long skuId);

    int deductStock(@Param("skuId") Long skuId, @Param("quantity") int quantity);

    int restoreStock(@Param("skuId") Long skuId, @Param("quantity") int quantity);
}
