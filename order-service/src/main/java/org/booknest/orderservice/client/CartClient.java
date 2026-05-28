package org.booknest.orderservice.client;

import org.booknest.orderservice.dto.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CART-SERVICE")
public interface CartClient {

    @GetMapping("/cart-service/cart/{cartId}")
     CartDto getCart(@PathVariable("cartId") Long cartId);

}
