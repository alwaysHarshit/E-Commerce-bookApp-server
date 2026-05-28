package org.booknest.orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDTO {
    private Long bookId;
    private int quantity;
}
