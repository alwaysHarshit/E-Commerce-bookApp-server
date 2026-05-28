package org.booknest.cartservice.model;

import lombok.Data;

@Data
public class BookResponse {
    private String id;
    private String title;
    private  double price;

}
