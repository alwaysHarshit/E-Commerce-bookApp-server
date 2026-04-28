package org.booknest.cartservice.service;

import org.booknest.cartservice.entity.Cart;

import java.util.List;

public interface CartService {
    Cart getCartByUser(int userId);
    Cart addItem(int userId, String booId);
    Cart removeItem(int userId, String bookId);
    Cart updateQuantity(int userId, String bookId, int quantity);
    void clearCart(int userId);
    double cartTotal(Cart  cart);
    List<Cart> getAllCarts();
}
