package org.booknest.orderservice.dto;

import lombok.Getter;
import org.booknest.orderservice.enums.OrderStatus;

@Getter
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}
