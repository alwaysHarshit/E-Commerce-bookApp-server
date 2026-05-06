package org.booknest.orderservice.dto;

import lombok.Builder;
import lombok.Data;
import org.booknest.orderservice.enums.OrderStatus;
import org.booknest.orderservice.enums.PaymentType;

import java.security.PrivateKey;
import java.util.List;

@Builder
@Data
public class OrderRequestDTO {
    private  Long AddressId;
    private List<OrderItemDTO> items;
    private PaymentType paymentType;

}

