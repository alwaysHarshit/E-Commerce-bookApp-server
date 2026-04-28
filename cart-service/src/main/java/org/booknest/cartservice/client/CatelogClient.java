package org.booknest.cartservice.client;

import org.booknest.cartservice.config.FeignConfig;
import org.booknest.cartservice.model.ApiResponse;
import org.booknest.cartservice.model.BookResponse;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "catelogClient",
        url = "http://localhost:8082",
        configuration = FeignConfig.class
)
public interface CatelogClient {

    @GetMapping("/api/books/{bookid}")
    ApiResponse<BookResponse> getBook(@PathVariable String bookid);
}
