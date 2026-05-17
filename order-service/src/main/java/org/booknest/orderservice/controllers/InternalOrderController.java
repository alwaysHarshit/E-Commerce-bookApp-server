package org.booknest.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.booknest.orderservice.dto.PaymentStatusUpdateRequest;
import org.booknest.orderservice.model.ApiResponse;
import org.booknest.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/orders")
@RequiredArgsConstructor
@Tag(name = "Internal Order Controller", description = "Internal APIs for service-to-service communication")
public class InternalOrderController {

    private final OrderService orderService;

    @PutMapping("/{orderId}/payment-status")
    @Operation(summary = "Update order payment status (Internal)", description = "Called by Payment Service to update status based on Stripe events")
    public ResponseEntity<ApiResponse<String>> updatePaymentStatus(@PathVariable Long orderId, @RequestBody PaymentStatusUpdateRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payment status updated successfully",
                        orderService.updatePaymentStatus(orderId, request)
                )
        );
    }
}
