package org.booknest.orderservice.service;

import org.booknest.orderservice.dto.*;
import org.booknest.orderservice.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    CheckoutResponseDto buyNow(BuyNowRequestDto request);
    CheckoutResponseDto checkoutCart(CheckoutCartRequestDto request);
    List<OrderResponseDTO> getMyOrders();
    OrderResponseDTO getOrderDetails(Long orderId);
    void cancelOrder(Long orderId);
    boolean hasPurchased(Long userId, Long bookId);
    String updatePaymentStatus(Long orderId, PaymentStatusUpdateRequest request);


    // Admin APIs
    Page<OrderResponseDTO> getAllOrders(Pageable pageable);
    String updateOrderStatus(Long orderId,UpdateOrderStatusRequest request);




}
