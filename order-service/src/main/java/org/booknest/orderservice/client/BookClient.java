package org.booknest.orderservice.client;

import org.booknest.orderservice.dto.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "book-service")
public interface BookClient {
    @GetMapping("/api/books/{id}")
    BookDto getBookById(@PathVariable("id") Long id);
}
