package org.booknest.paymentservice.service;

import org.booknest.paymentservice.dto.PaymentRequestDto;
import org.booknest.paymentservice.dto.PaymentResponseDto;

public interface PaymentService {

   String  createPaymentIntent(PaymentRequestDto paymentRequestDto );
   String handleStripeWebhook(String payload, String sigHeader);
   PaymentResponseDto getPaymentByOrderId(Long orderId);
   String cancelPayment(String paymentIntentId);
}
