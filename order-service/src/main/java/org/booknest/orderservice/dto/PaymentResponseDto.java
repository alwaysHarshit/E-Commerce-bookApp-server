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
    private PaymentStatus status;
}
