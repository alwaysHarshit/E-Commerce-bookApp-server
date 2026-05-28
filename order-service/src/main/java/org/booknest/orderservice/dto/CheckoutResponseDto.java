package org.booknest.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.booknest.orderservice.enums.OrderStatus;
import org.booknest.orderservice.enums.PaymentStatus;
import org.booknest.orderservice.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CheckoutResponseDto {

    private Long orderId;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String clientSecret;
}