package org.booknest.orderservice.service;

import org.booknest.orderservice.dto.AddressDto;
import org.booknest.orderservice.dto.BookDto;
import org.booknest.orderservice.dto.OrderRequestDTO;
import org.booknest.orderservice.entity.OrderEntity;
import org.booknest.orderservice.entity.OrderItemEntity;
import org.booknest.orderservice.enums.OrderStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    @Override
    public List<OrderEntity> getAllOrder() {
        return List.of();
    }

    @Override
    public OrderEntity updateOrderStatus(Long orderId, OrderStatus newStatus, String remark) {
        return null;
    }

    @Override
    public OrderEntity placeOrder(OrderRequestDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        // fetch the user form spring security context
        Long userId = Long.parseLong(userDetails.getUsername());

        //call user service(auth service) to get adress
        AddressDto address=userServiceClient.getAddressById(request.getAddressId());

        //iterate over the list of item from order to get book details
        List<OrderItemEntity> orderItemEntities = request.getItems().stream()
                .map(item -> {
                    BookDto book = bookServiceClient.getBookById(item.getBookId());

                    return OrderItemEntity.builder()
                            .bookId(item.getBookId())
                            .bookTitle(book.getBookTitle())
                            .price(book.getPrice())
                            .quantity(item.getQuantity())
                            .build();
                }).toList();

        //calculate the total price of order
        double totalAmount = orderItemEntities.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        //call payement service
       Long paymentId = paymentServiceClient.initiatePayment(userId,totalAmount, request.getPaymentType());

       //create the order obj
        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .orderedItems(orderItemEntities)
                .orderDatetime(LocalDateTime.now())
                .totalAmount(totalAmount)
                .paymentType(request.getPaymentType())
                .paymentId(paymentId)
                .orderStatus(OrderStatus.PENDING)
                .updateDate(LocalDateTime.now())
                .build();
        //set refernce of order in each item
        orderItemEntities.forEach(item -> item.setOrderE(order));

        //store the the ordered item in order entity list
        order.setOrderedItems(orderItemEntities);

        return order;
    }

    @Override
    public List<OrderEntity> getOrdersByUserId(Long userId) {

        return List.of();
    }

    @Override
    public OrderEntity cancelOrder(Long orderId, String reason) {
        return null;
    }

    @Override
    public List<OrderEntity> getOrdersByStatus(OrderStatus status) {
        return List.of();
    }
}
