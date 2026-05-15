package org.booknest.orderservice.client;

import org.booknest.orderservice.enums.PaymentType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payments/initiate")
    Long initiatePayment(
            @RequestParam Long userId,
            @RequestParam Double amount,
            @RequestParam PaymentType paymentType);
}
