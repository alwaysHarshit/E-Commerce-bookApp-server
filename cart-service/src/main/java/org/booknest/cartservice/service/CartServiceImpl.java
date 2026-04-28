package org.booknest.cartservice.service;

import lombok.extern.slf4j.Slf4j;
import org.booknest.cartservice.client.CatelogClient;
import org.booknest.cartservice.entity.Cart;
import org.booknest.cartservice.exception.CartNotFound;
import org.booknest.cartservice.repo.CartRepository;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CartServiceImpl implements  CartService {

    private final CartRepository cartRepo;
    private CatelogClient catelogClient;

    public CartServiceImpl(CartRepository cartRepo, CatelogClient catelogClient) {
        this.cartRepo = cartRepo;
        this.catelogClient = catelogClient;
    }

    @Override
    public Cart getCartByUser(int userId) {
        return cartRepo
                .findByUserId(userId)
                .orElseThrow(() -> new CartNotFound("Cart not found for user: " + userId));
    }

    @Override
    public Cart addItem(int userId, String bookId) {
        //find the cart using userId
        Cart dbCart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFound("Cart not found for user: " + userId));

        // call catelog service to get book detailed to add add in cart
        HttpEntity<?> book = catelogClient.getBook(bookId);
        log.debug(book.toString());
        return null;
    }

    @Override
    public Cart removeItem(int userId, String bookId) {
        return null;
    }

    @Override
    public Cart updateQuantity(int userId, String bookId, int quantity) {
        return null;
    }

    @Override
    public void clearCart(int userId) {

    }

    @Override
    public double cartTotal(Cart cart) {
        return 0;
    }

    @Override
    public List<Cart> getAllCarts() {
        return List.of();
    }
}
