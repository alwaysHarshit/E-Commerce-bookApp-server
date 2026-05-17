package org.booknest.orderservice.client;

import org.booknest.orderservice.dto.PaymentIntentResponseDto;
import org.booknest.orderservice.dto.PaymentRequestDto;
import org.booknest.orderservice.dto.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @PostMapping("/payment-service/api/payments/create-intent")
    PaymentIntentResponseDto createPaymentIntent(@RequestBody PaymentRequestDto request);

    @GetMapping("/payment-service/api/payments/order/{orderId}")
    PaymentResponseDto getPaymentByOrderId(@PathVariable("orderId") Long orderId);
}
