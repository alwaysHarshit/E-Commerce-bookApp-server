package org.booknest.orderservice.dto;

import lombok.Data;
import org.booknest.orderservice.enums.PaymentType;

@Data
public class BuyNowRequestDto {

    private Long bookId;
    private Integer quantity;

    private Long addressId;

    private PaymentType paymentMethod;
}
