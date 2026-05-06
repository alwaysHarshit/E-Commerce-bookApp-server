package org.booknest.orderservice.service;

import org.booknest.orderservice.dto.OrderRequestDTO;
import org.booknest.orderservice.entity.OrderEntity;
import org.booknest.orderservice.enums.OrderStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface OrderService {
    /* ----------- admin service ----------*/
    List<OrderEntity> getAllOrder();

    OrderEntity updateOrderStatus(Long orderId, OrderStatus newStatus, String remark);

    /* ------------ user services +++++++++++++ */

    OrderEntity placeOrder(OrderRequestDTO request, @AuthenticationPrincipal UserDetails userDetails);

    List<OrderEntity> getOrdersByUserId(Long userId);

    OrderEntity cancelOrder(Long orderId, String reason);

    List<OrderEntity> getOrdersByStatus(OrderStatus status);

}
