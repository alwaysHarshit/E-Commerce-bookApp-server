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
@Schema(description = "Data Transfer Object for payment response")
public class PaymentResponseDto {
    @Schema(description = "Internal payment ID", example = "1")
    private Long paymentId;

    @Schema(description = "Stripe Client screte that intiate payement intent at client side")
    private String clientSecret;

    @Schema(description = "Current status of the payment", example = "SUCCESS")
    private PaymentStatus status;
}
