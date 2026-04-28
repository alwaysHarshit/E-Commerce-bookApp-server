package org.booknest.cartservice.client;

import org.booknest.cartservice.config.FeignConfig;
import org.booknest.cartservice.model.UserBookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CATALOG-SERVICE",configuration = FeignConfig.class)
public interface CatelogClient {

    @GetMapping("/api/books/{bookid}")
    public ResponseEntity<?> getBook(@PathVariable String bookid);
}
