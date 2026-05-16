package org.booknest.orderservice.client;

import org.booknest.orderservice.dto.PaymentRequestDto;
import org.booknest.orderservice.dto.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @PostMapping("/api/payments/create-intent")
    PaymentResponseDto createPaymentIntent(@RequestBody PaymentRequestDto request);
}
