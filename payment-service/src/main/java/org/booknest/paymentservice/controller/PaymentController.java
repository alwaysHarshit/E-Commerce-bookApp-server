package org.booknest.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.booknest.paymentservice.dto.PaymentIntentResponseDto;
import org.booknest.paymentservice.dto.PaymentRequestDto;
import org.booknest.paymentservice.dto.PaymentResponseDto;
import org.booknest.paymentservice.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Controller", description = "Endpoints for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @CrossOrigin(origins = "http://localhost:5173/")
    @PostMapping("/create-intent")
    @Operation(summary = "Create a Payment Intent", description = "Creates a Stripe Payment Intent for a given order amount and currency.")
    @ApiResponse(responseCode = "200", description = "Payment Intent created successfully")
    public ResponseEntity<PaymentIntentResponseDto> createIntent(@RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentIntentResponseDto paymentIntent = paymentService.createPaymentIntent(paymentRequestDto);
        return ResponseEntity.ok(paymentIntent);
    }

    @PostMapping("/webhook")
    @Operation(summary = "Handle Stripe Webhook", description = "Handles asynchronous events from Stripe (e.g., payment success/failure).")
    @ApiResponse(responseCode = "200", description = "Webhook handled successfully")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @Parameter(description = "Stripe-Signature header for validation") @RequestHeader("Stripe-Signature") String sigHeader) {
        String s = paymentService.handleStripeWebhook(payload, sigHeader);
        return ResponseEntity.ok(s);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get Payment by Order ID", description = "Retrieves payment details for a specific order.")
    @ApiResponse(responseCode = "200", description = "Payment details found")
    @ApiResponse(responseCode = "404", description = "Payment not found for the given order ID")
    public ResponseEntity<PaymentResponseDto> getPaymentByOrderId(@Parameter(description = "ID of the order") @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @PostMapping("/cancel/{paymentIntentId}")
    @Operation(summary = "Cancel Payment", description = "Cancels a Stripe Payment Intent.")
    @ApiResponse(responseCode = "200", description = "Payment cancelled successfully")
    public ResponseEntity<String> cancelPayment(@Parameter(description = "Stripe Payment Intent ID") @PathVariable String paymentIntentId) {
        return ResponseEntity.ok(paymentService.cancelPayment(paymentIntentId));
    }

    @GetMapping("/all-payments")
    public ResponseEntity<Page<PaymentResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(paymentService.getAllPayments(pageable));
    }
}
