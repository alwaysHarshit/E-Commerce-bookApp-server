package org.booknest.orderservice.dto;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String isbn;
    private Double price;
}
