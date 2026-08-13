package com.team.ecommerce.trade.cart.service;

import com.team.ecommerce.trade.cart.dto.AddCartItemRequest;
import com.team.ecommerce.trade.cart.dto.CartView;
import com.team.ecommerce.trade.cart.dto.UpdateCartItemRequest;

public interface CartService {
    CartView getCart(Long userId);
    CartView addItem(Long userId, AddCartItemRequest request);
    CartView updateItem(Long userId, Long itemId, UpdateCartItemRequest request);
    void deleteItem(Long userId, Long itemId);
    void deleteSelected(Long userId);
}
