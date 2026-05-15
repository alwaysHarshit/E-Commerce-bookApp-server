package org.booknest.orderservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;
    private final BookClient bookClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final JwtUtils jwtUtils;
    private final HttpServletRequest requestHeader;

    // Helper to get current userId from JWT
    private Long getCurrentUserId() {
        String authHeader = requestHeader.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtils.extractUserId(token);
        }
        // Fallback for testing or if missing (should be handled by security filter in production)
        return 1L; 
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Long userId = getCurrentUserId();
        List<OrderItemEntity> orderItems = new ArrayList<>();
        double totalAmount = 0;

        for (OrderItemDTO itemDto : request.getItems()) {
            // 1. Fetch Book Snapshot
            BookDto book = bookClient.getBookById(itemDto.getBookId());
            
            // 2. Validate Inventory
            boolean hasStock = inventoryClient.checkStock(itemDto.getBookId(), itemDto.getQuantity());
            if (!hasStock) {
                throw new InsufficientStockException("Stock unavailable for book: " + book.getTitle());
            }

            // 3. Create Item Snapshot
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

        // 4. Initiate Payment
        Long paymentId = paymentClient.initiatePayment(userId, totalAmount, request.getPaymentMethod());

        // 5. Save Order
        OrderEntity order = OrderEntity.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .shippingAddressId(request.getAddressId())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .paymentId(paymentId)
                .orderStatus(OrderStatus.PENDING)
                .build();

        for (OrderItemEntity item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        OrderEntity savedOrder = orderRepo.save(order);

        // 6. Reduce Inventory
        for (OrderItemDTO itemDto : request.getItems()) {
            inventoryClient.reduceStock(itemDto.getBookId(), itemDto.getQuantity());
        }

        return orderMapper.toResponseDTO(savedOrder);
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

        if (!order.getOrderStatus().equals(OrderStatus.PENDING) && 
            !order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
            throw new RuntimeException("Order cannot be cancelled at this stage");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        
        // Restore Stock
        for (OrderItemEntity item : order.getItems()) {
            inventoryClient.restoreStock(item.getBookId(), item.getQuantity());
        }

        return orderMapper.toResponseDTO(orderRepo.save(order));
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepo.findAll(pageable).map(orderMapper::toResponseDTO);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus status) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setOrderStatus(status);
        return orderMapper.toResponseDTO(orderRepo.save(order));
    }

    @Override
    public boolean hasPurchased(Long userId, Long bookId) {
        return orderRepo.existsByUserIdAndOrderStatusAndItems_BookId(userId, OrderStatus.DELIVERED, bookId);
    }
}
