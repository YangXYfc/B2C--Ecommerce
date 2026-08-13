package com.team.ecommerce.trade.order.mapper;

import org.apache.ibatis.annotations.Mapper;

/** E-role read-only access to product and product_sku for order creation. */
@Mapper
public interface ProductReadMapper {

    SkuInfo selectSkuInfo(Long skuId);

    record SkuInfo(Long skuId, String skuName, java.math.BigDecimal price,
            Long productId, String productName, String productImage, Long merchantId) {}
}
