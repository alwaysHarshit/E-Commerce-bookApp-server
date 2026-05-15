package org.booknest.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Schema(description = "Data Transfer Object for updating payment status")
public class PaymentStatusUpdateRequest {

    @Schema(description = "New status of the payment", example = "COMPLETED")
    private String paymentStatus;

    @Schema(description = "Time when the payment status was updated")
    private LocalDateTime paymentTime;

}
