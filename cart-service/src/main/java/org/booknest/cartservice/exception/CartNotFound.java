package org.booknest.cartservice.exception;

public class CartNotFound extends RuntimeException {
    public CartNotFound(String message) {
        super(message);
    }
}
