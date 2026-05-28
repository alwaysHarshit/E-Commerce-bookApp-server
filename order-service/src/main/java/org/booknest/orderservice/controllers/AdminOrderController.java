package org.booknest.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.booknest.orderservice.dto.OrderResponseDTO;
import org.booknest.orderservice.dto.UpdateOrderStatusRequest;
import org.booknest.orderservice.model.ApiResponse;
import org.booknest.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin Order Controller", description = "Admin APIs for managing all orders")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders (Paginated)")
    public ResponseEntity<ApiResponse<Page<OrderResponseDTO>>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Succesfuly fetched all orders",
                        orderService.getAllOrders(pageable)
                )
        );
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<String>> updateStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "",
                        orderService.updateOrderStatus(orderId,request)
                )
        );
    }
}
