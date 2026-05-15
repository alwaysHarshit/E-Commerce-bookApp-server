package org.booknest.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long bookId;
    private String bookTitle;
    private String isbn;
    private Double bookPrice;
    private Integer quantity;
    private Double subtotal;
}
