package org.booknest.paymentservice.exception;

public class StripeServiceException extends RuntimeException {
    public StripeServiceException(String message) {
        super(message);
    }
}
