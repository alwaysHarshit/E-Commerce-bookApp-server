package org.booknest.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object for payment request")
public class PaymentRequestDto {

    @Schema(description = "ID of the order", example = "123")
    private Long orderId;

    @Schema(description = "Amount to be paid", example = "99.99")
    private double amount;

    @Schema(description = "Currency for the payment", example = "usd")
    private String currency;
}
