package org.booknest.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.booknest.orderservice.dto.BuyNowRequestDto;
import org.booknest.orderservice.dto.CheckoutCartRequestDto;
import org.booknest.orderservice.dto.CheckoutResponseDto;
import org.booknest.orderservice.dto.OrderResponseDTO;
import org.booknest.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "User APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout/")
    @Operation(summary = "Create a new order", description = "Supports both 'Buy Now")
    public ResponseEntity<CheckoutResponseDto> createOrder(@RequestBody CheckoutCartRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkoutCart(request));
    }
    @PostMapping("/checkout/buy-now")
    @Operation(summary = "Buy a single book instantly")
    public ResponseEntity<CheckoutResponseDto> buyNow(@RequestBody BuyNowRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.buyNow(request));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current user's orders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get single order details", description = "User can only access their own orders")
    public ResponseEntity<OrderResponseDTO> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order", description = "Only allowed if order is not shipped or delivered")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }
}
