package org.booknest.paymentservice.mapper;

import org.booknest.paymentservice.dto.PaymentResponseDto;
import org.booknest.paymentservice.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

   public PaymentResponseDto mapToPaymentReponse(Payment payment) {

        return PaymentResponseDto.builder()
                .id(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .paymentIntentId(payment.getPaymentIntentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .build();
    }
}
