package com.team.ecommerce.trade.cart.service;

import com.team.ecommerce.common.error.FeatureNotImplementedException;
import com.team.ecommerce.trade.cart.dto.AddCartItemRequest;
import com.team.ecommerce.trade.cart.dto.CartView;
import com.team.ecommerce.trade.cart.dto.UpdateCartItemRequest;
import org.springframework.stereotype.Service;

@Service
public class CartServiceSkeleton implements CartService {
    public CartView getCart(Long userId) { throw pending("cart.get"); }
    public CartView addItem(Long userId, AddCartItemRequest request) { throw pending("cart.addItem"); }
    public CartView updateItem(Long userId, Long itemId, UpdateCartItemRequest request) { throw pending("cart.updateItem"); }
    public void deleteItem(Long userId, Long itemId) { throw pending("cart.deleteItem"); }
    public void deleteSelected(Long userId) { throw pending("cart.deleteSelected"); }

    private FeatureNotImplementedException pending(String operation) {
        return new FeatureNotImplementedException(operation);
    }
}
