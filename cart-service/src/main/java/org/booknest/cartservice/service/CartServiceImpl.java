package org.booknest.cartservice.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.booknest.cartservice.client.CatelogClient;
import org.booknest.cartservice.entity.Cart;
import org.booknest.cartservice.entity.CartItem;
import org.booknest.cartservice.model.BookResponse;
import org.booknest.cartservice.repo.CartRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;
    private final CatelogClient catelogClient;

    public CartServiceImpl(CartRepository cartRepo, CatelogClient catelogClient) {
        this.cartRepo = cartRepo;
        this.catelogClient = catelogClient;
    }

    @Override
    public Cart getCartByUser(Long userId) {

        //find the cart using userId if not found then build a new cart
        return cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    // build new cart assocatied to that user
                    Cart build = Cart.builder()
                            .userId(userId)
                            .totalPrice(0.0d)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepo.save(build);

                });
    }

    @Override
    @Transactional
    public Cart addItem(Long userId, String bookId) {

        // get the cart first
        Cart cartByUser = this.getCartByUser(userId);

        Optional<CartItem> existingItem = cartByUser.getItems().stream()
                .filter(item -> item.getBookId().equals(bookId)).findFirst();

        //check if book already present in that cart then increase the count
        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + 1);
        } else {

            // call catelog service to get book detailed to add add in cart
            BookResponse book = catelogClient.getBook(bookId).getData();

            //creating a new cart item
            CartItem item = CartItem.builder()
                    .bookId(bookId)
                    .bookTitle(book.getTitle())
                    .price(book.getPrice())
                    .quantity(1)
                    .cart(cartByUser)
                    .build();

            //store in cart list
            cartByUser.getItems().add(item);
        }
        //update the total price
        double sum = cartByUser.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        cartByUser.setTotalPrice(sum);

        //store the cart item in db  item automatically save
        return cartRepo.save(cartByUser);
    }

    @Override
    @Transactional
    public Cart removeItem(Long userId, String bookId) {

        Cart cart = this.getCartByUser(userId);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();

            if (item.getQuantity() > 1) {
                //  decrease quantity
                item.setQuantity(item.getQuantity() - 1);
            } else {
                // remove properly (both sides)
                cart.getItems().remove(item);
                item.setCart(null);
            }
        }
        //  recalculate total
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        cart.setTotalPrice(total);

        return cartRepo.save(cart);
    }

    @Override
    public Cart updateQuantity(Long userId, String bookId, int quantity) {
        Cart cartByUser = this.getCartByUser(userId);

        cartByUser.getItems().stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        //  recalculate total
        double total = cartByUser.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        cartByUser.setTotalPrice(total);

        return cartRepo.save(cartByUser);

    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = this.getCartByUser(userId);

        cart.getItems().forEach(item -> item.setCart(null));
        cart.getItems().clear();

        cart.setTotalPrice(0.0);

        cartRepo.save(cart);
    }

    @Override
    public List<Cart> getAllCarts() {
        return cartRepo.findAll();
    }
}