package com.team.ecommerce.trade.cart.controller;

import com.team.ecommerce.common.api.ApiResponse;
import com.team.ecommerce.trade.cart.dto.AddCartItemRequest;
import com.team.ecommerce.trade.cart.dto.CartView;
import com.team.ecommerce.trade.cart.dto.UpdateCartItemRequest;
import com.team.ecommerce.trade.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) { this.cartService = cartService; }

    @GetMapping
    public ApiResponse<CartView> getCart(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(cartService.getCart(userId));
    }

    @PostMapping("/items")
    public ApiResponse<CartView> addItem(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddCartItemRequest request) {
        return ApiResponse.success(cartService.addItem(userId, request));
    }

    @PutMapping("/items/{id}")
    public ApiResponse<CartView> updateItem(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id, @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success(cartService.updateItem(userId, id, request));
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<Void> deleteItem(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        cartService.deleteItem(userId, id);
        return ApiResponse.success();
    }

    @DeleteMapping("/selected")
    public ApiResponse<Void> deleteSelected(@RequestHeader("X-User-Id") Long userId) {
        cartService.deleteSelected(userId);
        return ApiResponse.success();
    }
}
