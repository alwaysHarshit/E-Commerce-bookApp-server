package org.booknest.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for payment request")
public class PaymentRequestDto {
    @Schema(description = "ID of the order", example = "123")
    private Long orderId;
    
    @Schema(description = "Amount to be paid", example = "99.99")
    private double amount;
    
    @Schema(description = "Currency for the payment", example = "usd")
    private String currency;
}
