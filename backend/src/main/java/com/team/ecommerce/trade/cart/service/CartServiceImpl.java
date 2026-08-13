package com.team.ecommerce.trade.cart.service;

import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.common.error.ErrorCode;
import com.team.ecommerce.trade.cart.dto.AddCartItemRequest;
import com.team.ecommerce.trade.cart.dto.CartItemView;
import com.team.ecommerce.trade.cart.dto.CartView;
import com.team.ecommerce.trade.cart.dto.UpdateCartItemRequest;
import com.team.ecommerce.trade.cart.entity.CartEntity;
import com.team.ecommerce.trade.cart.mapper.CartMapper;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;

    public CartServiceImpl(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    @Override
    public CartView getCart(Long userId) {
        var items = cartMapper.selectCartItemViews(userId);
        var selectedAmount = items.stream()
                .filter(CartItemView::selected)
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartView(items, selectedAmount);
    }

    @Override
    @Transactional
    public CartView addItem(Long userId, AddCartItemRequest request) {
        // Check if this SKU already in user's cart
        var existing = cartMapper.selectByUserId(userId).stream()
                .filter(e -> e.productSkuId().equals(request.skuId()))
                .findFirst();

        if (existing.isPresent()) {
            var entity = existing.get();
            var updated = new CartEntity(entity.id(), entity.userId(), entity.productSkuId(),
                    entity.quantity() + request.quantity(), entity.selected(),
                    entity.createdAt(), entity.updatedAt());
            cartMapper.updateById(updated);
        } else {
            var entity = new CartEntity(null, userId, request.skuId(),
                    request.quantity(), 1, null, null);
            cartMapper.insert(entity);
        }
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartView updateItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        var entity = cartMapper.selectById(itemId);
        if (entity == null || !entity.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "购物车项不存在");
        }
        var updated = new CartEntity(
                entity.id(), entity.userId(), entity.productSkuId(),
                request.quantity() != null ? request.quantity() : entity.quantity(),
                request.selected() != null ? (request.selected() ? 1 : 0) : entity.selected(),
                entity.createdAt(), entity.updatedAt());
        cartMapper.updateById(updated);
        return getCart(userId);
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        int rows = cartMapper.deleteById(itemId, userId);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "购物车项不存在");
        }
    }

    @Override
    @Transactional
    public void deleteSelected(Long userId) {
        cartMapper.deleteSelectedByUserId(userId);
    }
}
