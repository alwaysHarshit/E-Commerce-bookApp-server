package org.booknest.paymentservice.execptions;

public class PaymentObejctNotFound extends RuntimeException {
    public PaymentObejctNotFound(String message) {
        super(message);
    }
}
