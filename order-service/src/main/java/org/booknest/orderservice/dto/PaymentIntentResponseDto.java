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
@Schema(description = "Data Transfer Object for payment intent response")
public class PaymentIntentResponseDto {
    @Schema(description = "Internal payment ID", example = "1")
    private Long paymentId;

    @Schema(description = "Stripe Client secret that initiates payment intent at client side")
    private String clientSecret;

    @Schema(description = "Current status of the payment", example = "INITIATED")
    private PaymentStatus status;
}
