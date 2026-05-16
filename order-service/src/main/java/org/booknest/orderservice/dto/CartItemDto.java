package org.booknest.orderservice.dto;

import lombok.Data;

@Data
public class CartItemDto {

    private Long bookId;
    private Integer quantity;

}
