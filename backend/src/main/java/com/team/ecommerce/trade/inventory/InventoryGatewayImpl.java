package com.team.ecommerce.trade.inventory;

import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.common.error.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InventoryGatewayImpl implements InventoryGateway {

    private final TradeProductSkuMapper productSkuMapper;

    public InventoryGatewayImpl(TradeProductSkuMapper productSkuMapper) {
        this.productSkuMapper = productSkuMapper;
    }

    @Override
    @Transactional
    public void check(List<InventoryItem> items) {
        for (var item : items) {
            Integer stock = productSkuMapper.selectStockForUpdate(item.skuId());
            if (stock == null) {
                throw new BusinessException(ErrorCode.INVALID_ARGUMENT,
                        "SKU不存在: " + item.skuId());
            }
            if (stock < item.quantity()) {
                throw new BusinessException(ErrorCode.INVALID_ARGUMENT,
                        "库存不足: SKU " + item.skuId() + " 需 " + item.quantity() + " 但仅剩 " + stock);
            }
        }
    }

    @Override
    @Transactional
    public void deduct(List<InventoryItem> items) {
        for (var item : items) {
            int rows = productSkuMapper.deductStock(item.skuId(), item.quantity());
            if (rows != 1) {
                throw new BusinessException(ErrorCode.INVALID_ARGUMENT,
                        "扣减库存失败: SKU " + item.skuId() + " 库存不足 " + item.quantity());
            }
        }
    }

    @Override
    @Transactional
    public void restore(List<InventoryItem> items) {
        for (var item : items) {
            productSkuMapper.restoreStock(item.skuId(), item.quantity());
        }
    }
}
