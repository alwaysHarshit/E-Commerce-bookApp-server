package org.booknest.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import org.booknest.paymentservice.enums.PaymentStatus;

@Data
@Builder
@Schema(description = "Data Transfer Object for payment response")
public class PaymentResponseDto {

    @Schema(description = "Internal payment ID", example = "1")
    private Long id;

    @Schema(description = "ID of the order", example = "123")
    private Long orderId;

    @Schema(description = "Stripe Payment Intent ID", example = "pi_123456789")
    private String paymentIntentId;

    @Schema(description = "Paid amount", example = "99.99")
    private double amount;

    @Schema(description = "Currency of the payment", example = "usd")
    private String currency;

    @Schema(description = "Current status of the payment", example = "SUCCESS")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
