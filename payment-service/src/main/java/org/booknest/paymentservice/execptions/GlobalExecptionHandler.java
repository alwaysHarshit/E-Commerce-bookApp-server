package org.booknest.paymentservice.execptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExecptionHandler {

    @ExceptionHandler(StripeExecption.class)
    public ResponseEntity<String> handleStripeExecption(StripeExecption e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ExceptionHandler(PaymentObejctNotFound.class)
    public ResponseEntity<String> handlePaymentObejctNotFound(PaymentObejctNotFound e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
