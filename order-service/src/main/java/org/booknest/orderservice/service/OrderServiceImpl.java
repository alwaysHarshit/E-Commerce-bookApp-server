package org.booknest.orderservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.booknest.orderservice.client.BookClient;
import org.booknest.orderservice.client.InventoryClient;
import org.booknest.orderservice.client.PaymentClient;
import org.booknest.orderservice.dto.*;
import org.booknest.orderservice.entity.OrderEntity;
import org.booknest.orderservice.entity.OrderItemEntity;
import org.booknest.orderservice.enums.OrderStatus;
import org.booknest.orderservice.enums.PaymentStatus;
import org.booknest.orderservice.exception.InsufficientStockException;
import org.booknest.orderservice.exception.ResourceNotFoundException;
import org.booknest.orderservice.mapper.OrderMapper;
import org.booknest.orderservice.repo.OrderRepo;
import org.booknest.orderservice.utils.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;
    private final BookClient bookClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;


private Long getCurrentUserId() {
    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
    return Long.parseLong(userId);
}


    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Long userId = getCurrentUserId();
        List<OrderItemEntity> orderItems = new ArrayList<>();
        double totalAmount = 0;

        for (OrderItemDTO itemDto : request.getItems()) {
            BookDto book = bookClient.getBookById(itemDto.getBookId());
            boolean hasStock = inventoryClient.checkStock(itemDto.getBookId(), itemDto.getQuantity());
            if (!hasStock) {
                throw new InsufficientStockException("Stock unavailable for book: " + book.getTitle());
            }

            OrderItemEntity item = OrderItemEntity.builder()
                    .bookId(book.getId())
                    .bookTitle(book.getTitle())
                    .isbn(book.getIsbn())
                    .bookPrice(book.getPrice())
                    .quantity(itemDto.getQuantity())
                    .subtotal(book.getPrice() * itemDto.getQuantity())
                    .build();
            
            orderItems.add(item);
            totalAmount += item.getSubtotal();
        }

        // 1. Save Order with PENDING_PAYMENT status first to get an ID
        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .shippingAddressId(request.getAddressId())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.INITIATED)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .build();

        for (OrderItemEntity item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        OrderEntity savedOrder = orderRepo.save(order);

        // 2. Initiate Payment (Stripe Intent creation via Payment Service)
        PaymentRequestDto paymentRequest = PaymentRequestDto.builder()
                .orderId(savedOrder.getId())
                .amount(totalAmount)
                .currency("usd") // Default currency
                .build();

        try {
            PaymentResponseDto paymentResponse = paymentClient.createPaymentIntent(paymentRequest);
            savedOrder.setPaymentId(paymentResponse.getId());
            savedOrder.setPaymentIntentId(paymentResponse.getPaymentIntentId());
            orderRepo.save(savedOrder);
            log.info("Payment intent created for Order ID: {}. PaymentIntentId: {}", savedOrder.getId(), paymentResponse.getPaymentIntentId());
        } catch (Exception e) {
            log.error("Failed to initiate payment for Order ID: {}", savedOrder.getId(), e);
            // Optionally handle rollback or mark as failed
        }

        // 3. Reduce Inventory (Reservations)
        for (OrderItemDTO itemDto : request.getItems()) {
            inventoryClient.reduceStock(itemDto.getBookId(), itemDto.getQuantity());
        }

        return orderMapper.toResponseDTO(savedOrder);
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
            order.setPaidAt(request.getPaymentTime() != null ? request.getPaymentTime() : LocalDateTime.now());
            log.info("Payment SUCCESS for order {}. Status updated to CONFIRMED.", orderId);
        } else if (newStatus == PaymentStatus.FAILED) {
            log.error("Payment FAILED for order {}.", orderId);
            // In a real app, we might want to keep it in PENDING_PAYMENT or CANCEL
        } else if (newStatus == PaymentStatus.CANCELED) {
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
                inventoryClient.restoreStock(item.getBookId(), item.getQuantity());
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
