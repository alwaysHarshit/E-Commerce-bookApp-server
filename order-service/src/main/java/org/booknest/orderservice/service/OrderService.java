package org.booknest.orderservice.service;

import org.booknest.orderservice.dto.*;
import org.booknest.orderservice.dto.OrderResponseDTO;
import org.booknest.orderservice.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    CheckoutResponseDto buyNow(BuyNowRequestDto request);
    CheckoutResponseDto checkoutCart(CheckoutCartRequestDto request);
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
