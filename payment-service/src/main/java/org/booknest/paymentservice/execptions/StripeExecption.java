package org.booknest.paymentservice.execptions;

public class StripeExecption extends RuntimeException {
    public StripeExecption(String message) {
        super(message);
    }
}
