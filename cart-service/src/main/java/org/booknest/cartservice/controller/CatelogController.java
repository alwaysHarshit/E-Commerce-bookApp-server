package org.booknest.cartservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.booknest.cartservice.entity.Cart;
import org.booknest.cartservice.service.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart Controller", description = "Endpoints for managing the shopping cart")
public class CatelogController {
    @Autowired
    private CartServiceImpl cartService;

    @GetMapping("/getBook/{bookId}")
    @Operation(summary = "Add a book to the cart", description = "Adds a book to the user's cart by its ID")
    public ResponseEntity<Cart> addNewBook(@PathVariable String bookId){
        Cart cart = cartService.addItem(getUserId(), bookId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/delete/{bookId}")
    @Operation(summary = "Remove a book from the cart", description = "Removes a book from the user's cart by its ID")
    public ResponseEntity<Cart> delete(@PathVariable String bookId) {
        Cart cart = cartService.removeItem(getUserId(), bookId);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/update/{bookId}")
    @Operation(summary = "Update book quantity", description = "Updates the quantity of a specific book in the user's cart")
    public ResponseEntity<Cart> updateQuantity(@PathVariable String bookId, @RequestParam(name = "quantity") int quantity) {

        Cart updatedCart = cartService.updateQuantity(getUserId(), bookId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear the cart", description = "Removes all items from the user's cart")
    public ResponseEntity<String> clearCart() {
        cartService.clearCart(getUserId());
        return ResponseEntity.ok("Cart cleared successfully");
    }


    @GetMapping("/all")
    @Operation(summary = "Get all carts", description = "Retrieves all carts in the system (Admin only/Internal use)")
    public ResponseEntity<List<Cart>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    //helper method
    private long getUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(userId);
    }


}
