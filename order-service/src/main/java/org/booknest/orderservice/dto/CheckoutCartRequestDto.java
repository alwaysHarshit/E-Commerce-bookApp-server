package org.booknest.orderservice.dto;

import lombok.Data;
import org.booknest.orderservice.enums.PaymentType;

@Data
public class CheckoutCartRequestDto {

    private Long cartId;
    private Long addressId;
    private PaymentType paymentMethod;
}
