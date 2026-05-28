package org.booknest.orderservice.mapper;

import org.booknest.orderservice.dto.OrderItemResponseDTO;
import org.booknest.orderservice.dto.OrderResponseDTO;
import org.booknest.orderservice.entity.OrderEntity;
import org.booknest.orderservice.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseDTO toResponseDTO(OrderEntity order) {
        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .addressId(order.getShippingAddressId())
                .paymentType(order.getPaymentType())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .items(order.getItems().stream()
                        .map(this::toItemResponseDTO)
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemResponseDTO toItemResponseDTO(OrderItemEntity item) {
        return OrderItemResponseDTO.builder()
                .bookId(item.getBookId())
                .bookTitle(item.getBookTitle())
                .isbn(item.getIsbn())
                .bookPrice(item.getBookPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}
