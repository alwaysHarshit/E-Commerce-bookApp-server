package org.booknest.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.booknest.orderservice.enums.OrderStatus;
import org.booknest.orderservice.enums.PaymentStatus;
import org.booknest.orderservice.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderResponseDTO {

    private Long orderId;

    private Long userId;

    private Double totalAmount;

    private Long addressId;

    private PaymentType paymentType;

    private PaymentStatus paymentStatus;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private List<OrderItemResponseDTO> items;
}