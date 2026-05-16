package org.booknest.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.booknest.paymentservice.enums.PaymentStatus;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentResponseDto {

    private Long paymentId;
    private String clientSecret;
    private PaymentStatus status;
}
