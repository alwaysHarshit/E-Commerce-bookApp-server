package org.booknest.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.booknest.orderservice.enums.PaymentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for detailed payment response")
public class PaymentResponseDto {
    @Schema(description = "Internal payment ID", example = "1")
    private Long id;

    @Schema(description = "Associated Order ID", example = "101")
    private Long orderId;

    @Schema(description = "Stripe-generated Payment Intent ID", example = "pi_12345")
    private String paymentIntentId;

    @Schema(description = "Transaction amount", example = "29.99")
    private double amount;

    @Schema(description = "Transaction currency", example = "inr")
    private String currency;

    @Schema(description = "Current status of the payment", example = "SUCCESS")
    private PaymentStatus status;
}
