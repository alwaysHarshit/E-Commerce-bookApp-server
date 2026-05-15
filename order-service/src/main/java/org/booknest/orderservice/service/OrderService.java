package org.booknest.orderservice.service;

import org.booknest.orderservice.dto.OrderRequestDTO;
import org.booknest.orderservice.dto.OrderResponseDTO;
import org.booknest.orderservice.dto.PaymentStatusUpdateRequest;
import org.booknest.orderservice.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO request);
    List<OrderResponseDTO> getMyOrders();
    OrderResponseDTO getOrderDetails(Long orderId);
    OrderResponseDTO cancelOrder(Long orderId);
    
    // Admin APIs
    Page<OrderResponseDTO> getAllOrders(Pageable pageable);
    OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status);
    
    // Internal API
    boolean hasPurchased(Long userId, Long bookId);
    void updatePaymentStatus(Long orderId, PaymentStatusUpdateRequest request);
}
