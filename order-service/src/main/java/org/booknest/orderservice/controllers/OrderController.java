package org.booknest.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.booknest.orderservice.dto.*;
import org.booknest.orderservice.model.ApiResponse;
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
    public ResponseEntity<ApiResponse<CheckoutResponseDto>> createOrder(@RequestBody CheckoutCartRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Successfully placed cart order",
                        orderService.checkoutCart(request)
                ));

    }

    @PostMapping("/checkout/buy-now")
    @Operation(summary = "Buy a single book instantly")
    public ResponseEntity<ApiResponse<CheckoutResponseDto>> buyNow(@RequestBody BuyNowRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Successfully placed order",
                        orderService.buyNow(request)
                ));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current user's orders")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getMyOrders() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Orders fetched successfully",
                        orderService.getMyOrders()
                )
        );
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get single order details", description = "User can only access their own orders")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderDetails(@PathVariable Long orderId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order details fetched successfully",
                        orderService.getOrderDetails(orderId)
                )
        );
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order", description = "Only allowed if order is not shipped or delivered")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order cancelled successfully",
                        null
                )
        );
    }


    @GetMapping("/has-purchased")
    @Operation(summary = "Check if user has purchased a book", description = "Returns true only if the user has a DELIVERED order containing the book")
    public ResponseEntity<ApiResponse<Boolean>> hasPurchased(@RequestParam Long userId, @RequestParam Long bookId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Successfully checked purchase",
                        orderService.hasPurchased(userId, bookId)
                )
        );
    }
}
