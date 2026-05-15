package org.booknest.orderservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.booknest.orderservice.dto.PaymentStatusUpdateRequest;
import org.booknest.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/orders")
@RequiredArgsConstructor
@Tag(name = "Internal Order Controller", description = "Internal APIs for other microservices")
public class InternalOrderController {

    private final OrderService orderService;

    @GetMapping("/has-purchased")
    @Operation(summary = "Check if user has purchased a book", description = "Returns true only if the user has a DELIVERED order containing the book")
    public ResponseEntity<Map<String, Boolean>> hasPurchased(
            @RequestParam Long userId, 
            @RequestParam Long bookId) {
        boolean purchased = orderService.hasPurchased(userId, bookId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("purchased", purchased);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/payment-status")
    @Operation(summary = "Update order payment status", description = "Called by Payment Service to update status based on Stripe events")
    public ResponseEntity<Void> updatePaymentStatus(
            @PathVariable Long orderId,
            @RequestBody PaymentStatusUpdateRequest request) {
        orderService.updatePaymentStatus(orderId, request);
        return ResponseEntity.ok().build();
    }
}
