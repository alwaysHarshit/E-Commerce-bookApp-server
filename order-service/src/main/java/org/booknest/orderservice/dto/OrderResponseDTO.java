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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private Double totalAmount;
    private Long addressId;
    private PaymentType paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private List<OrderItemResponseDTO> items;
    private LocalDateTime createdAt;
}
