package org.booknest.orderservice.client;

import org.booknest.orderservice.dto.BookDto;
import org.booknest.orderservice.model.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "CATALOG-SERVICE")
public interface CatalogClient {

    @GetMapping("/catalog-service/api/books/{id}")
    ApiResponse<BookDto> getBookById(@PathVariable("id") Long id);

    @PutMapping("/catalog-service/api/inventory/reduce")
    void reduceStock(@RequestParam Long bookId, @RequestParam Integer quantity);

    @PutMapping("/catalog-service/api/inventory/restore")
    void restoreStock(@RequestParam Long bookId, @RequestParam Integer quantity);

    @GetMapping("/catalog-service/api/inventory/check")
    boolean checkStock(@RequestParam Long bookId, @RequestParam Integer quantity);
}
