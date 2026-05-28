package org.booknest.paymentservice.dto;

import lombok.*;
import org.booknest.paymentservice.enums.PaymentStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntentResponseDto {

    private Long paymentId;
    private String clientSecret;
    private PaymentStatus status;
}
