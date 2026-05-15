package org.booknest.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for updating payment status")
public class PaymentStatusUpdateRequest {
    @Schema(description = "New status of the payment", example = "SUCCESS")
    private String paymentStatus;
    
    @Schema(description = "Time when the payment status was updated")
    private LocalDateTime paymentTime;
}
