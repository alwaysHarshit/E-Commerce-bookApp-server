package org.booknest.orderservice.dto;

import lombok.Getter;

@Getter
public class BookDto {
    private Long bookId;
    private String bookTitle;
    private  double Price;
    private int quantity;
}
