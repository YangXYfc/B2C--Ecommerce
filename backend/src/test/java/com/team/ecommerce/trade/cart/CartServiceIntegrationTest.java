package com.team.ecommerce.trade.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.trade.cart.dto.AddCartItemRequest;
import com.team.ecommerce.trade.cart.dto.UpdateCartItemRequest;
import com.team.ecommerce.trade.cart.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

    private static final Long USER_ID = 4L;

    @Test
    void getCartReturnsItemsAndSelectedAmount() {
        var cart = cartService.getCart(USER_ID);
        // data.sql: user 4 has 3 cart items
        assertEquals(3, cart.items().size());
        // selected items: id1 (4999*1) + id2 (59*2) = 5117
        assertEquals(0, cart.selectedAmount().compareTo(new java.math.BigDecimal("5117")));
    }

    @Test
    void addNewSkuCreatesCartItem() {
        // SKU 2 (智选Pro 白色) is not in user 4's cart
        var cart = cartService.addItem(USER_ID, new AddCartItemRequest(2L, 1));
        assertTrue(cart.items().stream().anyMatch(i -> i.skuId().equals(2L) && i.quantity() == 1));
    }

    @Test
    void addExistingSkuIncrementsQuantity() {
        // SKU 1 already in cart with quantity 1
        var cart = cartService.addItem(USER_ID, new AddCartItemRequest(1L, 2));
        assertTrue(cart.items().stream().anyMatch(i -> i.skuId().equals(1L) && i.quantity() == 3));
    }

    @Test
    void updateItemChangesQuantityAndSelection() {
        var cart = cartService.getCart(USER_ID);
        var first = cart.items().get(0);
        var updated = cartService.updateItem(USER_ID, first.id(),
                new UpdateCartItemRequest(5, false));
        var changed = updated.items().stream().filter(i -> i.id().equals(first.id())).findFirst().orElseThrow();
        assertEquals(5, changed.quantity());
        assertFalse(changed.selected());
    }

    @Test
    void deleteItemRemovesFromCart() {
        var cart = cartService.getCart(USER_ID);
        var first = cart.items().get(0);
        cartService.deleteItem(USER_ID, first.id());
        var after = cartService.getCart(USER_ID);
        assertEquals(2, after.items().size());
    }

    @Test
    void deleteItemOfAnotherUserFails() {
        assertThrows(BusinessException.class,
                () -> cartService.deleteItem(USER_ID, 4L)); // item 4 belongs to user 5
    }

    @Test
    void deleteSelectedRemovesOnlyCheckedItems() {
        cartService.deleteSelected(USER_ID);
        var after = cartService.getCart(USER_ID);
        // only the unselected item (id 3) remains
        assertEquals(1, after.items().size());
        assertFalse(after.items().get(0).selected());
    }
}
