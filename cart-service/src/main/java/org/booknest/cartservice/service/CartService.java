package org.booknest.cartservice.service;

import org.booknest.cartservice.entity.Cart;

import java.util.List;

public interface CartService {
    Cart getCartByUser(Long userId);
    Cart getCartByCartId(Long cartId);
    Cart addItem(Long userId, String bookId);
    Cart removeItem(Long userId, String bookId);
    Cart updateQuantity(Long userId, String bookId, int quantity);
    void clearCart(Long userId);
    List<Cart> getAllCarts();
}
