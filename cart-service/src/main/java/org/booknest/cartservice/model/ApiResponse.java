package org.booknest.cartservice.model;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;
}
