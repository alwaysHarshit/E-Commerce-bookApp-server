package org.booknest.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.booknest.orderservice.client.CartClient;
import org.booknest.orderservice.client.CatalogClient;
import org.booknest.orderservice.client.PaymentClient;
import org.booknest.orderservice.dto.BookDto;
import org.booknest.orderservice.dto.*;
import org.booknest.orderservice.entity.OrderEntity;
import org.booknest.orderservice.entity.OrderItemEntity;
import org.booknest.orderservice.enums.OrderStatus;
import org.booknest.orderservice.enums.PaymentStatus;
import org.booknest.orderservice.exception.InsufficientStockException;
import org.booknest.orderservice.exception.PaymentException;
import org.booknest.orderservice.exception.ResourceNotFoundException;
import org.booknest.orderservice.mapper.OrderMapper;
import org.booknest.orderservice.repo.OrderRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;
    private final CatalogClient catalogClient;
    private final PaymentClient paymentClient;
    private final CartClient cartClient;


    private Long getCurrentUserId() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    return Long.parseLong(userId);
}


    @Override
    public CheckoutResponseDto buyNow(BuyNowRequestDto request) {


        /* call catalog service to fetch the book reques deatils */
            BookDto book = catalogClient.getBookById(request.getBookId()).getData();

            //call the catalog service to check whether sufficenet quanity is in stock or not
            boolean hasStock = catalogClient.checkStock(request.getBookId(), request.getQuantity());

            if (!hasStock) throw new InsufficientStockException("Stock unavailable for book: " + book.getTitle());

            //create the orderItem entity
            OrderItemEntity item = OrderItemEntity.builder()
                    .bookId(book.getId())
                    .bookTitle(book.getTitle())
                    .isbn(book.getIsbn())
                    .bookPrice(book.getPrice())
                    .quantity(request.getQuantity())
                    .subtotal(book.getPrice() * request.getQuantity())
                    .build();

            //create the order enity
            OrderEntity newOrder = OrderEntity.builder()
                    .userId(getCurrentUserId())
                    .totalAmount(item.getSubtotal())
                    .shippingAddressId(request.getAddressId())
                    .orderStatus(OrderStatus.PENDING_PAYMENT)
                    .paymentType(request.getPaymentMethod())
                    .items(List.of(item))
                    .build();

            item.setOrder(newOrder);

            // saving the order entity in db
            OrderEntity createdOrder = orderRepo.save(newOrder);

        try {
            //call the payment service to create payment intent
            //creating payement request
            PaymentRequestDto build = PaymentRequestDto.builder()
                    .orderId(createdOrder.getId())
                    .amount(createdOrder.getTotalAmount())
                    .currency("inr").build();

            PaymentResponseDto paymentIntent = paymentClient.createPaymentIntent(build);

            //update db state with payment realted info
            createdOrder.setPaymentId(paymentIntent.getPaymentId());
            createdOrder.setPaymentStatus(paymentIntent.getStatus());
            orderRepo.save(createdOrder);

            //call catalog service to reduce the inventory
            catalogClient.reduceStock(createdOrder.getId(), request.getQuantity());

            //bulding checkout response and return
            return CheckoutResponseDto.builder()
                    .orderId(createdOrder.getId())
                    .orderStatus(newOrder.getOrderStatus())
                    .paymentStatus(paymentIntent.getStatus())
                    .clientSecret(paymentIntent.getClientSecret())
                    .build();

        }
        catch (Exception e) {
            createdOrder.setOrderStatus(OrderStatus.CANCELLED);
            orderRepo.save(createdOrder);
            throw new PaymentException("Payment service unavailable");
        }


    }

    @Override
    public CheckoutResponseDto checkoutCart(CheckoutCartRequestDto request) {

        //call the cart service to get the cart
        List<CartItemDto> orderItems = cartClient.getCart(request.getCartId()).getItems();

        double totalAmount = 0;
        List<OrderItemEntity> orderItemEntities = new ArrayList<>();

        //iterated over the cart items and build order item entities
        for (CartItemDto item : orderItems) {
            BookDto book = catalogClient.getBookById(item.getBookId()).getData();
            boolean hasStock = catalogClient.checkStock(item.getBookId(), item.getQuantity());

            if (!hasStock) {
                throw new InsufficientStockException("Stock unavailable for book: " + book.getTitle());
            }

            OrderItemEntity newOrderItem = OrderItemEntity.builder()
                    .bookId(book.getId())
                    .bookTitle(book.getTitle())
                    .isbn(book.getIsbn())
                    .bookPrice(book.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(book.getPrice() * item.getQuantity())
                    .build();

            orderItemEntities.add(newOrderItem);
            totalAmount+=item.getQuantity()* book.getPrice();
        }

        //create the order enity
        OrderEntity newOrder = OrderEntity.builder()
                .userId(getCurrentUserId())
                .totalAmount(totalAmount)
                .shippingAddressId(request.getAddressId())
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .paymentType(request.getPaymentMethod())
                .items(orderItemEntities)
                .build();


        for (OrderItemEntity cartItem : orderItemEntities) {
            cartItem.setOrder(newOrder);
        }

        // saving the order entity in db
        OrderEntity createdOrder = orderRepo.save(newOrder);


        try {
            //call the payment service to create payment intent
            //creating payement request
            PaymentRequestDto build = PaymentRequestDto.builder()
                    .orderId(createdOrder.getId())
                    .amount(createdOrder.getTotalAmount())
                    .currency("inr").build();

            PaymentResponseDto paymentIntent = paymentClient.createPaymentIntent(build);

            //update db state with payment realted info
            createdOrder.setPaymentId(paymentIntent.getPaymentId());
            createdOrder.setPaymentStatus(paymentIntent.getStatus());
            orderRepo.save(createdOrder);

            //call catalog service to reduce the inventory
            for (CartItemDto orderItem : orderItems) {
                catalogClient.reduceStock(orderItem.getBookId(), orderItem.getQuantity());
            }

            //bulding checkout response and return
            return CheckoutResponseDto.builder()
                    .orderId(createdOrder.getId())
                    .orderStatus(newOrder.getOrderStatus())
                    .paymentStatus(paymentIntent.getStatus())
                    .clientSecret(paymentIntent.getClientSecret())
                    .build();

        }
        catch (Exception e) {
            createdOrder.setOrderStatus(OrderStatus.CANCELLED);
            orderRepo.save(createdOrder);
            throw new PaymentException("Payment service unavailable");
        }
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long orderId, PaymentStatusUpdateRequest request) {
        log.info("Updating payment status for Order ID: {} to {}", orderId, request.getPaymentStatus());
        
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Idempotency check: if order is already paid, ignore duplicate updates
        if (order.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.warn("Order {} is already marked as SUCCESS. Skipping duplicate update.", orderId);
            return;
        }

        // Map String status to Enum
        PaymentStatus newStatus;
        try {
            newStatus = PaymentStatus.valueOf(request.getPaymentStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid payment status received: {}", request.getPaymentStatus());
            return;
        }

        // Validate state transition
        validatePaymentTransition(order, newStatus);

        order.setPaymentStatus(newStatus);
        
        if (newStatus == PaymentStatus.SUCCESS) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
            log.info("Payment SUCCESS for order {}. Status updated to CONFIRMED.", orderId);
        }
        else if (newStatus == PaymentStatus.FAILED) {
            log.error("Payment FAILED for order {}.", orderId);
            order.setPaymentStatus(newStatus);
            order.setOrderStatus(OrderStatus.CANCELLED);
            log.info("Payment FAILED for order {}. Status updated to CANCELLED.", orderId);
        }
        else if (newStatus == PaymentStatus.CANCELED) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            restoreInventory(order);
            log.info("Payment CANCELED for order {}. Status updated to CANCELLED.", orderId);
        }

        orderRepo.save(order);
    }


    private void validatePaymentTransition(OrderEntity order, PaymentStatus newStatus) {
        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot update payment for an already DELIVERED order");
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED && newStatus == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot pay for a CANCELLED order");
        }
    }

    private void restoreInventory(OrderEntity order) {
        log.info("Restoring inventory for order {}", order.getId());
        for (OrderItemEntity item : order.getItems()) {
            try {
                catalogClient.restoreStock(item.getBookId(), item.getQuantity());
            } catch (Exception e) {
                log.error("Failed to restore stock for book {} in order {}", item.getBookId(), order.getId(), e);
            }
        }
    }

    @Override
    public List<OrderResponseDTO> getMyOrders() {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(getCurrentUserId()).stream()
                .map(orderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderDetails(Long orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("Unauthorized access to order");
        }
        return orderMapper.toResponseDTO(order);
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT) && 
            !order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
            throw new RuntimeException("Order cannot be cancelled at this stage");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELED);
        
        restoreInventory(order);

        return orderMapper.toResponseDTO(orderRepo.save(order));
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable).map(orderMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        log.info("Updating order status for {} from {} to {}", orderId, order.getOrderStatus(), status);
        order.setOrderStatus(status);
        return orderMapper.toResponseDTO(orderRepo.save(order));
    }

    @Override
    public boolean hasPurchased(Long userId, Long bookId) {
        return orderRepo.existsByUserIdAndOrderStatusAndItems_BookId(userId, OrderStatus.DELIVERED, bookId);
    }
}
